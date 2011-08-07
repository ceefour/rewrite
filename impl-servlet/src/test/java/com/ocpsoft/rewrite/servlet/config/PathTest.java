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
package com.ocpsoft.rewrite.servlet.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ocpsoft.rewrite.config.Operation;
import com.ocpsoft.rewrite.event.Rewrite;
import com.ocpsoft.rewrite.mock.MockEvaluationContext;
import com.ocpsoft.rewrite.mock.MockRewrite;
import com.ocpsoft.rewrite.servlet.config.parameters.DefaultConverter;
import com.ocpsoft.rewrite.servlet.config.parameters.DefaultValidator;
import com.ocpsoft.rewrite.servlet.config.parameters.El;
import com.ocpsoft.rewrite.servlet.config.parameters.PathParameterBuilder;
import com.ocpsoft.rewrite.servlet.http.impl.HttpInboundRewriteImpl;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class PathTest
{
   private Rewrite rewrite;
   private HttpServletRequest request;

   @Before
   public void before()
   {
      request = EasyMock.createNiceMock(HttpServletRequest.class);

      EasyMock.expect(request.getRequestURI())
               .andReturn("/context/application/path").anyTimes();

      EasyMock.expect(request.getContextPath())
               .andReturn("/context").anyTimes();

      EasyMock.replay(request);

      rewrite = new HttpInboundRewriteImpl(request, null);
   }

   @Test
   public void testPathMatchesWithParameters()
   {
      Assert.assertTrue(Path.matches("/application/{seg}").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testPathAttemptsToBindParameters()
   {
      MockBinding mockBinding = new MockBinding();
      PathParameterBuilder path = Path.matches("/application/{seg}")
               .and("seg", mockBinding);
      MockEvaluationContext context = new MockEvaluationContext();
      Assert.assertTrue(path.evaluate(rewrite, context));

      List<Operation> operations = context.getPostOperations();
      Assert.assertEquals(1, operations.size());
      operations.get(0).perform(rewrite, context);

      Assert.assertTrue(mockBinding.isConverted());
      Assert.assertTrue(mockBinding.isValidated());
      Assert.assertTrue(mockBinding.isPerformed());
   }

   public void sandbox()
   {
      // ExtendedPath.matches("/path/{id;person.id;profile.id =~ /[0-9]+/}");
      // Path.matches("/path/{id:person.id:profile.id : [0-9]+ ");

      Path.matches("/path/{id}/{other}").withRequestParamBinding();

      Path.matches("/path/{id}/{other}").and("id");
      Path.matches("/path/{id}/{other}").and("id", "[0-9]+");
      Path.matches("/path/{id}/{other}").and("id", "[0-9]+", El.property("person.id"));
      Path.matches("/path/{id}/{other}").and("id", "[0-9]+", El.property("person.id", DefaultConverter.class));
      Path.matches("/path/{id}/{other}")
               .and("id", "[0-9]+", El.property("person.id", DefaultConverter.class, DefaultValidator.class));

      Path.matches("/path/{id}/{other}")
               .and("id")
               .matches("[0-9]+")
               .bindsTo(El.property("person.id").convertedBy(DefaultConverter.class)
                        .validatedBy(DefaultValidator.class))

               .and("other")
               .attemptBindTo(El.property("#{profile.id}"));
   }

   @Test
   public void testPathMatchesLiteral()
   {
      Assert.assertTrue(Path.matches("/application/path").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testPathMatchesPattern()
   {
      Assert.assertTrue(Path.matches("/application/.*").evaluate(rewrite, new MockEvaluationContext()));
   }

   @Test
   public void testDoesNotMatchNonHttpRewrites()
   {
      Assert.assertFalse(Path.matches("/blah").evaluate(new MockRewrite(), new MockEvaluationContext()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullCausesException()
   {
      Path.matches(null);
   }
}
