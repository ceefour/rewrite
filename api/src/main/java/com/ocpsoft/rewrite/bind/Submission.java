/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ocpsoft.rewrite.bind;

import com.ocpsoft.rewrite.context.EvaluationContext;
import com.ocpsoft.rewrite.event.Rewrite;

/**
 * Defines the methods necessary to perform, converted, validated {@link Binding} value submission.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface Submission
{
   /**
    * Convert the given value into the expected type.
    */
   Object convert(Rewrite event, EvaluationContext context, Object value);

   /**
    * Return true if the given value passes all validations.
    */
   boolean validates(Rewrite event, EvaluationContext context, Object value);

   /**
    * Store a value into the designated storage location.
    */
   Object submit(Rewrite event, EvaluationContext context, Object value);
}
