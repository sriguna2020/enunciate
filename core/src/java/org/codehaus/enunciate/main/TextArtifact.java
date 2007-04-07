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

package org.codehaus.enunciate.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * An artifact that is some text.
 *
 * @author Ryan Heaton
 */
public class TextArtifact extends BaseArtifact {

  private final String text;

  public TextArtifact(String module, String id, String text) {
    super(module, id);
    this.text = text;
  }

  /**
   * The text for this artifact.
   *
   * @return The text for this artifact.
   */
  public String getText() {
    return text;
  }

  /**
   * Exports its text to the specified file.
   *
   * @param file The file to export the text to.
   */
  public void exportTo(File file, Enunciate enunciate) throws IOException {
    if (file.exists() && file.isDirectory()) {
      file = new File(file, getId());
    }
    
    file.getParentFile().mkdirs();
    PrintWriter writer = new PrintWriter(file);
    writer.print(this.text);
    writer.flush();
    writer.close();
  }
}