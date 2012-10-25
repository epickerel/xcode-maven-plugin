/*
 * #%L
 * xcode-maven-plugin
 * %%
 * Copyright (C) 2012 SAP AG
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.sap.prd.mobile.ios.mios;

import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

import com.sap.prd.mobile.ios.mios.task.XCodePrepareBuildTask;

/**
 * Prepares the local build environment. Copies and unpacks the artifacts of the referenced projects
 * into the target folder.
 * 
 * @goal prepare-xcode-build
 * @requiresDependencyResolution
 */
public class XCodePrepareMojo extends AbstractXCodeMojo
{

  /**
   * @parameter expression="${project}"
   * @readonly
   * @required
   */
  public MavenProject project;

  /**
   * @component role="org.codehaus.plexus.archiver.manager.ArchiverManager"
   * @required
   */
  private ArchiverManager archiverManager;

  /**
   * The entry point to Aether, i.e. the component doing all the work.
   * 
   * @component
   */
  protected RepositorySystem repoSystem;

  /**
   * The current repository/network configuration of Maven.
   * 
   * @parameter default-value="${repositorySystemSession}"
   * @readonly
   */
  protected RepositorySystemSession repoSession;

  /**
   * The project's remote repositories to use for the resolution of project dependencies.
   * 
   * @parameter default-value="${project.remoteProjectRepositories}"
   * @readonly
   */
  protected List<RemoteRepository> projectRepos;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException
  {

    Set<String> configurations = getConfigurations();
    Set<String> sdks = getSDKs();
    try {
      for (String configuration : configurations) {
        for (String sdk : sdks) {
          new XCodePrepareBuildTask().setLog(getLog()).setArchiverManager(archiverManager)
            .setRepositorySystem(repoSystem)
            .setRepositorySystemSession(repoSession).setProjectRepos(projectRepos).setMavenProject(project)
            .setConfiguration(configuration).setSdk(sdk).execute();
        }
      }
    }
    catch (XCodeException ex) {
      throw new MojoExecutionException("Cannot prepare build environment", ex);
    }
  }
}
