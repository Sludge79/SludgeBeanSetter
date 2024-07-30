package com.sludge.util;

import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import com.sludge.util.strategy.ConvertStrategy;
import com.sludge.util.strategy.api.InstanceProperty;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Sludge
 * @since 2024/6/28 15:21
 */
public class GenerateSetterAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(editor, project);

        if (psiFile == null) return;

        PsiElement elementAtCaret = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass sourcePsiClass = PsiTreeUtil.getParentOfType(elementAtCaret, PsiClass.class);

        if (sourcePsiClass == null) {
            Messages.showErrorDialog(project, "无法找到源类", "错误");
            return;
        }

        TreeClassChooser chooser = TreeClassChooserFactory.getInstance(project).createAllProjectScopeChooser("选择目标类");
        chooser.showDialog();
        PsiClass targetPsiClass = chooser.getSelected();


        if (targetPsiClass == null) return;

        try {
            String generatedCode = generateSetterMethods(sourcePsiClass, targetPsiClass);
            insertCodeAtCaret(editor, generatedCode);
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "生成代码时出错：" + ex.getMessage(), "错误");
        }
    }

    private void insertCodeAtCaret(Editor editor, String code) {
        Document document = editor.getDocument();
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();

        WriteCommandAction.runWriteCommandAction(editor.getProject(), () ->
                document.insertString(offset, code)
        );
    }

    public String generateSetterMethods(PsiClass sourceClass, PsiClass targetClass) {
        StringBuilder methodBuilder = new StringBuilder();
        String sourceInstanceName = "sourceInstance";
        String targetInstanceName = "targetInstance";

        methodBuilder
                .append("public static ")
                .append(sourceClass.getName())
                .append(" convertTo")
                .append(sourceClass.getName())
                .append("(")
                .append(targetClass.getName())
                .append(" ")
                .append(targetInstanceName)
                .append(") {\n")
                .append("\t\t")

                .append(sourceClass.getName())
                .append(" ")
                .append(sourceInstanceName)
                .append(" = new ")
                .append(sourceClass.getName())
                .append("();\n");

        Map<String, PsiMethod> sourceGetters = getGetterMethods(sourceClass);
        Map<String, PsiMethod> targetSetters = getSetterMethods(targetClass);
        Set<String> fills = new HashSet<>();

        for (String propertyName : sourceGetters.keySet()) {
            if (targetSetters.containsKey(propertyName)) {
                PsiMethod getter = sourceGetters.get(propertyName);
                PsiMethod setter = targetSetters.get(propertyName);

                if (Objects.isNull(getter.getReturnType())) break;

                String getterType = getter.getReturnType().getCanonicalText();
                String setterType = setter.getParameterList().getParameters()[0].getType().getCanonicalText();

                String getterMethod = getter.getName();
                String setterMethod = setter.getName();
                if (getter.getReturnType().equalsToText(setterType)) {
                    methodBuilder
                            .append("\t\t")
                            .append(sourceInstanceName)
                            .append(".")
                            .append(setterMethod)
                            .append("(")
                            .append(targetInstanceName)
                            .append(".")
                            .append(getterMethod)
                            .append("());\n");
                    fills.add(propertyName);
                } else {
                    String getName = getterMethod.substring(3);
                    String setName = setterMethod.substring(3);
                    //类型不同，属性名相同
                    if (StringUtil.equals(getName, setName)) {
                        boolean append = ConvertStrategy.append(methodBuilder, new InstanceProperty(getterType, setterType, sourceInstanceName, setterMethod, targetInstanceName, getterMethod));
                        if (append) {
                            fills.add(propertyName);
                        }
                    }
                }
            }
        }

        Set<String> all = sourceGetters.keySet();
        fills.forEach(all::remove);
        all.remove("Class");
        if (CollectionUtils.isNotEmpty(all)) {
            methodBuilder.append("\t\t//the following keys does not match on target.\n");
            all.forEach(key ->
                    methodBuilder
                            .append("\t\t//")
                            .append(key)
                            .append("\n")
            );
        }
        methodBuilder
                .append("\t\treturn ")
                .append(sourceInstanceName)
                .append(";\n")
                .append("\t}");
        return methodBuilder.toString();
    }


    private Map<String, PsiMethod> getGetterMethods(PsiClass psiClass) {
        Map<String, PsiMethod> getters = new HashMap<>();
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (isGetter(method)) {
                String propertyName = getPropertyName(method.getName());
                getters.put(propertyName, method);
            }
        }
        return getters;
    }

    private Map<String, PsiMethod> getSetterMethods(PsiClass psiClass) {
        Map<String, PsiMethod> setters = new HashMap<>();
        for (PsiMethod method : psiClass.getAllMethods()) {
            if (isSetter(method)) {
                String propertyName = getPropertyName(method.getName());
                setters.put(propertyName, method);
            }
        }
        return setters;
    }

    private boolean isGetter(PsiMethod method) {
        if (method.getName().startsWith("get") && method.getParameterList().isEmpty()) {
            method.getReturnType();
            return true;
        }
        return method.getName().startsWith("is") && method.getParameterList().isEmpty();
    }

    private boolean isSetter(PsiMethod method) {
        return method.getName().startsWith("set") && method.getParameterList().getParametersCount() == 1;
    }

    private String getPropertyName(String methodName) {
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return methodName.substring(3);
        }
        if (methodName.startsWith("is")) {
            return methodName.substring(2);
        }
        return methodName;
    }
}
