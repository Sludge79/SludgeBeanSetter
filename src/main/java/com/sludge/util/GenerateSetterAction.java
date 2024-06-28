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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilBase;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        for (String propertyName : sourceGetters.keySet()) {
            if (targetSetters.containsKey(propertyName)) {
                PsiMethod getter = sourceGetters.get(propertyName);
                PsiMethod setter = targetSetters.get(propertyName);

                if (Objects.isNull(getter.getReturnType())) break;
                if (getter.getReturnType().equalsToText(setter.getParameterList().getParameters()[0].getType().getCanonicalText())) {
                    String getterMethod = getter.getName();
                    String setterMethod = setter.getName();

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
                }
            }
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
