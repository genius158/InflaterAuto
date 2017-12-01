package com.yan.inflaterauto.compiler;


import com.google.auto.service.AutoService;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.yan.inflaterauto.annotation.Convert;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;

import javax.lang.model.util.Elements;
import javax.annotation.processing.Filer;
import javax.lang.model.util.Types;

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
    private HashMap<String, String> classMap;

    private final String ANDROID = "android.";
    private final String ANDROID_WIDGET = "android.widget.";

    private final String DEFAULT = "InfAuto";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        typesUtils = processingEnvironment.getTypeUtils();
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
            convert.types();
        } catch (MirroredTypesException e) {
            mte = e;
        }
        if (mte == null) {
            return;
        }

        for (TypeMirror typeMirror : mte.getTypeMirrors()) {
            TypeElement classTypeElement = (TypeElement) typesUtils.asElement(typeMirror);
            String fullName = classTypeElement.getQualifiedName().toString();
            String className = DEFAULT + classTypeElement.getSimpleName().toString();

            int cons = 12;// 1100
            if (fullName.contains(ANDROID)) {
                cons = 14;// 1110
            }
            if (fullName.contains(ANDROID_WIDGET)) {
                cons = 15;// 1111
            }
            generate(packageName, fullName, className, cons);
        }

        String[] classCons = convert.typesCount();

        for (String classCon : classCons) {
            if (!classCon.contains("|")) {
                continue;
            }

            String[] cc = classCon.split("\\|");
            final String classStr = cc[0];
            if (classStr.lastIndexOf(".") == -1 || classStr.lastIndexOf(".") + 1 > classStr.length()) {
                continue;
            }

            final String className = DEFAULT + classStr.substring(classStr.lastIndexOf(".") + 1, classStr.length());

            int cons = 14;
            if (cc.length >= 2) {
                cons = getConNum(cc[1]);
            }

            generate(packageName, classStr, className, cons);
        }
    }

    private void generate(String packageName, String fullName, String className, int cons) {
        String keyName = fullName;
        if (fullName.contains(ANDROID_WIDGET)) {
            keyName = keyName.replace(ANDROID_WIDGET, "");
        }
        classMap.put(keyName, packageName + "." + className);

        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.bestGuess(fullName));

        ClassName contextName = ClassName.bestGuess("android.content.Context");
        ClassName attributeSetName = ClassName.bestGuess("android.util.AttributeSet");

        try {
            if ((cons & 8) == 8) {
                MethodSpec constructor1 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(contextName, "context")
                        .addStatement("super(context)")
                        .build();
                builder.addMethod(constructor1);
            }
            if ((cons & 4) == 4) {
                MethodSpec constructor2 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(contextName, "context")
                        .addParameter(attributeSetName, "attributeSet")
                        .addStatement("super(context, attributeSet)")
                        .build();
                builder.addMethod(constructor2);
            }
            if ((cons & 2) == 2) {
                MethodSpec constructor3 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(contextName, "context")
                        .addParameter(attributeSetName, "attributeSet")
                        .addParameter(int.class, "defStyleAttr")
                        .addStatement("super(context, attributeSet, defStyleAttr)")
                        .build();
                builder.addMethod(constructor3);
            }
            if ((cons & 1) == 1) {
                MethodSpec constructor4 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(contextName, "context")
                        .addParameter(attributeSetName, "attributeSet")
                        .addParameter(int.class, "defStyleAttr")
                        .addParameter(int.class, "defStyleRes")
                        .addStatement("super(context, attributeSet, defStyleAttr,defStyleRes)")
                        .build();
                builder.addMethod(constructor4);
            }

            ClassName layoutParamsName = ClassName.get(packageName, "LayoutParams");
            MethodSpec generateLayoutParams = MethodSpec.methodBuilder("generateLayoutParams")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(layoutParamsName)
                    .addParameter(attributeSetName, "attrs")
                    .addStatement("$T vlp = ($T)super.generateLayoutParams(attrs)", layoutParamsName, layoutParamsName)
                    .addStatement("$T.autoLayout(vlp, getContext(), attrs)", ClassName.bestGuess("com.yan.inflaterauto.AutoUtils"))
                    .addStatement("return vlp")
                    .build();
            builder.addMethod(generateLayoutParams);

            JavaFile javaFile = JavaFile.builder(packageName, builder.build()).build();

            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getConNum(String strCons) {
        int c = 0;
        for (int i = 0; i < strCons.length(); i++) {
            Character cr = strCons.charAt(i);
            int icr = (int) (Integer.parseInt(cr.toString()) * Math.pow(2, strCons.length() - i - 1));
            c += icr;
        }
        return c;
    }

    private void makeConvert(Element element) {
        try {
            TypeElement classElement = (TypeElement) element;
            String fullName = elementUtils.getPackageOf(classElement).getQualifiedName().toString();
            String className = DEFAULT + classElement.getSimpleName().toString();

            ParameterizedTypeName mapName = ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(String.class), ClassName.get(String.class));
            MethodSpec.Builder getConvertMapBuild = MethodSpec.methodBuilder("getConvertMap")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(mapName)
                    .addStatement("$T classMap = new $T<>()", mapName, ClassName.get(HashMap.class));
            for (HashMap.Entry<String, String> entry : classMap.entrySet()) {
                getConvertMapBuild.addStatement("classMap.put($S,$S)", entry.getKey(), entry.getValue());
            }
            getConvertMapBuild.addStatement("return classMap");

            TypeSpec typeSpec = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.bestGuess("com.yan.inflaterauto.AutoConvert"))
                    .superclass(ClassName.get(classElement))
                    .addMethod(getConvertMapBuild.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(fullName, typeSpec).build();

            javaFile.writeTo(filer);
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
