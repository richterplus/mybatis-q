package com.github.mybatisq;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Mojo(name = "gencode", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GenCodeMojo extends AbstractMojo {

    /**
     * 被扫描的实体类所在的包
     */
    @Parameter(required = true)
    private String entityPackage;

    /**
     * 生成的代码所在的包
     */
    @Parameter(required = true)
    private String genPackage;

    /**
     * 文件编码（默认UTF-8）
     */
    @Parameter(defaultValue = "UTF-8")
    private String encoding;

    @Component
    private MavenProject project;

    private ClassLoader getClassLoader() throws MojoExecutionException {
        try {
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            classpathElements.add(project.getBuild().getTestOutputDirectory());
            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File((String) classpathElements.get(i)).toURI().toURL();
            }
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("entity package: " + entityPackage);
        getLog().info("gen package: " + genPackage);

        ClassLoader cl = getClassLoader();

        URL classPath = cl.getResource(entityPackage.replace(".", "/"));
        if (classPath == null) {
            getLog().error("Failed to load classpath");
            throw new MojoFailureException("Failed to load classpath");
        }

        File file = new File(classPath.getFile());
        if (file == null || file.listFiles() == null) {
            return;
        }

        List<Class<?>> classes = new ArrayList<>();
        for (File f : file.listFiles()) {
            try {
                classes.add(cl.loadClass(entityPackage + "." + f.getName().replace(".class", "")));
            } catch (ClassNotFoundException e) {
                getLog().error(e);
                throw new MojoFailureException("Can not resolve class name " + entityPackage + "." + f.getName().replace(".class", ""));
            }
        }

        URL outPath = cl.getResource(genPackage.replace(".", "/"));

        for (Class<?> c : classes) {
            getLog().info("entity class: " + c.getName());
        }

        try {
            InputStream input = cl.getResourceAsStream("com/github/mybatisq/QMapper.xml");
            String content = IOUtils.toString(input, encoding).replace("com.github.mybatisq", genPackage);
            input.close();
            String path = outPath.getFile() + "/QMapper.xml";
            FileUtils.write(new File(path), content, encoding);
            getLog().info(path);

            input = cl.getResourceAsStream("com/github/mybatisq/QMapper.java.q");
            content = IOUtils.toString(input, encoding).replace("com.github.mybatisq", genPackage);
            input.close();
            path = outPath.getFile() + "/QMapper.java";
            FileUtils.write(new File(path), content, encoding);
            getLog().info(path);
        } catch (IOException e) {
            getLog().error(e);
            throw new MojoFailureException("Failed to close input stream: com/github/mybatisq/QMapper.xml");
        }
    }
}
