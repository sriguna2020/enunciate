/*
 * Copyright 2006 Web Cohesion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.enunciate.contract.rest;

import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.util.Declarations;
import org.codehaus.enunciate.rest.annotations.Verb;
import net.sf.jelly.apt.Context;
import net.sf.jelly.apt.decorations.declaration.DecoratedClassDeclaration;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class declaration decorated as a REST endpoint.
 *
 * @author Ryan Heaton
 */
public class RESTEndpoint extends DecoratedClassDeclaration {

  //todo: support versioning a REST endpoint.

  private final Collection<RESTMethod> RESTMethods;
  private String baseURL;

  public RESTEndpoint(ClassDeclaration delegate) {
    super(delegate);

    ArrayList<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
    //first iterate through all direct superinterfaces and add their methods if they are annotated as a REST endpoint:
    for (InterfaceType interfaceType : delegate.getSuperinterfaces()) {
      InterfaceDeclaration interfaceDeclaration = interfaceType.getDeclaration();
      if ((interfaceDeclaration != null) && (interfaceDeclaration.getAnnotation(org.codehaus.enunciate.rest.annotations.RESTEndpoint.class) != null)) {
        for (MethodDeclaration methodDeclaration : interfaceDeclaration.getMethods()) {
          if (methodDeclaration.getAnnotation(Verb.class) != null) {
            methods.add(methodDeclaration);
          }
        }
      }
    }


    AnnotationProcessorEnvironment env = Context.getCurrentEnvironment();
    Declarations utils = env.getDeclarationUtils();

    CLASS_METHODS : for (MethodDeclaration methodDeclaration : delegate.getMethods()) {
      //first make sure that this method isn't just an implementation of an interface method already added.
      for (MethodDeclaration method : methods) {
        if (utils.overrides(methodDeclaration, method)) {
          break CLASS_METHODS;
        }
      }

      if ((methodDeclaration.getModifiers().contains(Modifier.PUBLIC)) && (methodDeclaration.getAnnotation(Verb.class) != null)) {
        methods.add(methodDeclaration);
      }
    }

    this.RESTMethods = new ArrayList<RESTMethod>();
    for (MethodDeclaration methodDeclaration : methods) {
      this.RESTMethods.add(new RESTMethod(methodDeclaration));
    }
  }

  /**
   * The rest methods on this REST endpoint.
   *
   * @return The rest methods on this REST endpoint.
   */
  public Collection<RESTMethod> getRESTMethods() {
    return RESTMethods;
  }

  /**
   * The base URL for REST endpoints.
   *
   * @return The base URL for REST endpoints.
   */
  public String getBaseURL() {
    return baseURL;
  }

  /**
   * The base URL for REST endpoints.
   *
   * @param baseURL The base URL for REST endpoints.
   */
  public void setBaseURL(String baseURL) {
    this.baseURL = baseURL;
  }

  /**
   * The current annotation processing environment.
   *
   * @return The current annotation processing environment.
   */
  protected AnnotationProcessorEnvironment getAnnotationProcessorEnvironment() {
    return Context.getCurrentEnvironment();
  }

}
