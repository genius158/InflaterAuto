package com.yan.inflaterauto.compiler;


import com.google.auto.service.AutoService;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.yan.inflaterauto.annotation.Convert;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import java.util.HashSet;
import java.util.HashMap;

/**
 * create by yan 28/11/2017
 */
@AutoService(Processor.class)
public class InflaterAutoProcessor extends AbstractProcessor {
    private Elements elementUtils;
    private Types typesUtils;
    private Filer filer;
    private Messager messager;
    private HashMap<String, String> classMap;

    private final String ANDROID_WIDGET = "android.widget.";
    private final String VIEW_GROUP = "android.view.ViewGroup";

    private final String DEFAULT = "InfAuto";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        typesUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Convert.class);
        for (Element element : elements) {
            if (element.getKind() == ElementKind.CLASS) {
                classMap = new HashMap<>();

                makeAutoViewGroup(element);
                makeConvert(element);
                return true;
            }
        }
        return false;
    }

    private void makeAutoViewGroup(Element element) {
        final String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();

        TypeElement tyElement = (TypeElement) element;
        Convert convert = tyElement.getAnnotation(Convert.class);
        MirroredTypesException mte = null;
        try {
            convert.value();
        } catch (MirroredTypesException e) {
            mte = e;
        }
        if (mte == null) {
            return;
        }

        for (TypeMirror typeMirror : mte.getTypeMirrors()) {
            TypeElement typeElement = (TypeElement) typesUtils.asElement(typeMirror);
            generate(packageName, typeElement);
        }
    }

    private void generate(String packageName, TypeElement element) {
        if (!typesUtils.isSubtype(element.asType(), elementUtils.getTypeElement(VIEW_GROUP).asType())) {
            messager.printMessage(Diagnostic.Kind.WARNING, element.getSimpleName().toString() + " is not a ViewGroup");
            return;
        }
        try {
            String fullName = element.getQualifiedName().toString();
            String simpleName = element.getSimpleName().toString();
            String className = DEFAULT + simpleName;

            String keyName = fullName;
            if (fullName.contains(ANDROID_WIDGET)) {
                keyName = keyName.replace(ANDROID_WIDGET, "");
            }

            TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ClassName.bestGuess(fullName));

            List<? extends Element> elements = elementUtils.getAllMembers(element);
            List<ExecutableElement> listExecutable = ElementFilter.constructorsIn(elements);

            for (ExecutableElement executable : listExecutable) {
                Set<Modifier> modifiers = element.getModifiers();
                if (modifiers.isEmpty() || (!modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.PROTECTED))) {
                    messager.printMessage(Diagnostic.Kind.WARNING, "check constructor in class " + simpleName);
                    continue;
                }
                MethodSpec.Builder constructor = MethodSpec.constructorBuilder().addModifiers(element.getModifiers());

                StringBuilder statement = new StringBuilder("super(");
                for (VariableElement variableElement : executable.getParameters()) {
                    String variableName = variableElement.getSimpleName().toString();
                    constructor.addParameter(ClassName.get(variableElement.asType()), variableName);
                    statement.append(variableName).append(",");
                }
                if (statement.toString().contains("super(,")) {
                    statement.delete(0, statement.length());
                } else {
                    statement.deleteCharAt(statement.lastIndexOf(",")).append(")");
                }
                constructor.addStatement(statement.toString());
                builder.addMethod(constructor.build());
            }

            ClassName layoutParamsName = ClassName.get(packageName, "LayoutParams");
            MethodSpec generateLayoutParams = MethodSpec.methodBuilder("generateLayoutParams")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(layoutParamsName)
                    .addParameter(ClassName.bestGuess("android.util.AttributeSet"), "attrs")
                    .addStatement("$T vlp = ($T)super.generateLayoutParams(attrs)", layoutParamsName, layoutParamsName)
                    .addStatement("$T.autoLayout(vlp, getContext(), attrs)", ClassName.bestGuess("com.yan.inflaterauto.AutoUtils"))
                    .addStatement("return vlp")
                    .build();
            builder.addMethod(generateLayoutParams);

            JavaFile javaFile = JavaFile.builder(packageName, builder.build()).build();
            javaFile.writeTo(filer);
            classMap.put(keyName, packageName + "." + className);

            System.out.print(":inflaterauto:" + packageName + "." + className + " is generated\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeConvert(Element element) {
        try {
            TypeElement classElement = (TypeElement) element;
            String convertName = "com.yan.inflaterauto.AutoConvert";
            if (classElement.getInterfaces() == null || !classElement.getInterfaces().contains(elementUtils.getTypeElement(convertName).asType())
                    || classElement.getModifiers().contains(Modifier.ABSTRACT)) {
                messager.printMessage(Diagnostic.Kind.ERROR, classElement.getSimpleName().toString()
                        + " must implement AutoConvert and can not modified with abstract");
                return;
            }

            String packageName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
            String className = DEFAULT + classElement.getSimpleName().toString();

            final ClassName nameMap = ClassName.get(HashMap.class);
            final ClassName nameStr = ClassName.get(String.class);
            ParameterizedTypeName mapName = ParameterizedTypeName.get(nameMap, nameStr, nameStr);
            MethodSpec.Builder getConvertMapBuild = MethodSpec.methodBuilder("getConvertMap")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(mapName)
                    .addStatement("$T classMap = new $T<>()", mapName, nameMap)
                    .addStatement("HashMap<String, String> superMap = super.getConvertMap()")
                    .addStatement("if(superMap != null){ classMap.putAll(superMap); }");
            for (HashMap.Entry<String, String> entry : classMap.entrySet()) {
                getConvertMapBuild.addStatement("classMap.put($S,$S)", entry.getKey(), entry.getValue());
            }
            getConvertMapBuild.addStatement("return classMap");

            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(ClassName.get(classElement.asType()))
                    .addSuperinterface(ClassName.bestGuess(convertName))
                    .addMethod(getConvertMapBuild.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
            javaFile.writeTo(filer);

            System.out.print(":inflaterauto:" + packageName + "." + className + " is generated\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>(1);
        set.add(Convert.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_7;
    }
}
