package org.jnario.feature.documentation

import static extension org.jnario.jnario.test.util.Helpers.*
import static extension org.jnario.lib.Each.*
import static extension org.jnario.lib.Should.*
import static extension org.jnario.jnario.test.util.FeatureExecutor.*

/*
 * Jnario Features are based on [Xtend](http://www.xtend-lang.org). It is a good idea
 * to read the Xtend [documentation](http://www.eclipse.org/xtend/documentation/index.html)
 * before you get started with Jnario, because expressions, fields, methods and extensions in Jnario work exactly as in Xtend.
 *  
 * ### Installation
 * 
 * Jnario currently requires [Eclipse](http://www.eclipse.org) 3.5 or higher with [Xtext](http://www.xtext.org) 2.3,  and a Java SDK version 5 or above. 
 * You can install the most recent version from this update site: `http://www.jnario.org/updates/snapshot/`. Jnario requires a small runtime library that contains
 * the JUnit integration. You can download the jar [here](http://jnario.org/updates/snapshot/org.jnario.lib-0.1.0-SNAPSHOT.jar).
 * 
 *   
 */
describe "Jnario Features - Tutorial"{
  
  /*
   * The first example for using Jnario Features is the specification of a calculator. Start out with creating
   * a new file (**File** -> **New** -> **File**) and giving it the file extension *.feature.
   * 
   * A feature file consists of the a feature and the scenarios that are the acceptance criteria for the feature.
   * For the calculator the the feature description in form of a story and one scenario for adding
   * numbers looks as follows:
   * 
   * <pre class="prettyprint lang-feature">
   * Feature: Calculator
   *     
   *   As a math dummy
   *   I want a calculator that helps me with basic math operations
   *   In order to check my capabilities
   * 
   *   Scenario: Adding two numbers
   *     
   *     Given a calculator
   *     When I enter two numbers and press add
   *     Then it should show the sum
   * </pre>
   * 
   * When you save the file an xtend-gen folder will be created and the corresponding generated java files will
   * be created. Those files can be executed as JUnit-Tests. Right click and select **Run As** -> **JUnit Test**
   * 
   * You will see the result of the run and it will be green. Since the steps from scenario are not implemented
   * yet the **When** and **Then** steps will be marked as _PENDING_ as an reminder that there is still work to do.
   */
   describe "How to write a Feature"{
     /*
     * A feature consists of the name of the feature and a number of scenarios. Those scenarios
     * are examples for the behavior that is expected from the feature. For the calculator those
     * could be scenarios like addition or subtraction.
     * 
     * Each scenario is defined by the steps that describe the expected behavior. **Given** steps give
     * all the necessary information about the context of the scenario. **When** describes the action
     * and **Then** describes the expected outcome that will be verified.
     * 
     * Necessary keywords for describing a feature are **Feature**, **Scenario**, **Given**, **When**, **Then**. The names
     * and the step descriptions are free text. You can define multiple steps with the same keyword.
     * If you don't want to repeat **Given** over and over again you can use the keyword **And** in exchange.
     * 
     * @filter('''|.executesSuccessfully) 
     * @lang(feature)
     */
    
     fact "Multiple steps"{
        '''
      package demo

      Feature: Calculator
        In order to check my capabilities
        As a math dummy
        I want a calculator that helps me with basic math operations
                
        Scenario: Dividing two numbers
      
          Given a calculator
          When I enter the number 70
          And I enter the number 10
          And I press divide
          Then it should show 7
        '''.executesSuccessfully
       }
       
     /*
        * After defining a scenario with its steps to be able to execute it the necessary code has
        * to be added to steps. For each step the implementation is done directly after the description
        * of the step. The language used to define the logic is [Xtend](http://www.eclipse.org/xtend/documentation/index.html#fields).
        * 
        * Running the feature file now will not be possible since the type Calculator is unknown.
        * Create a Java class with the name Calculator in the same project as your feature file.
        * Add the necessary import to the feature file or include it by auto completion while typing (CTRL + SPACE).
        * 
        * <span class="label label-info">Info</span> Specifying the package and declaring and importing other packages
        * works similar to Xtend (static imports work as well). 
        * 
        * @filter('''|.executesSuccessfully)
        * @lang(feature)
        */
       fact "Step definition"{
         '''
       package demo
       import tutorialtest.math.Calculator
       
       Feature: Calculator
       
       Scenario: Adding two numbers
         Given a calculator
           var calculator = new Calculator
         '''.executesSuccessfully
       }
       
       /*
        * In the **When** step the calculator has to be used to call a method on the object.
        * To define variables that can be used from the steps within one scenario declare them
        * as fields after the scenario description.
        * 
        * Also define a method add for the calculator that takes two int values as parameters and returns an int.
        * 
        * @filter('''|.executesSuccessfully)
        * @lang(feature)
        */
       fact "Define fields for steps"{
         '''
       package demo
       import tutorialtest.math.Calculator
       
       Feature: Calculator
       
       Scenario: Adding two numbers
         Calculator calculator
         Given a calculator
           calculator = new Calculator
         When I enter two numbers and press add
           var result = calculator.add(20,70)
         '''.executesSuccessfully
       }
       
       /*
        * The **Then** step will be the behavior assertion. For writing readable short assertions
        * different additional assertions come with Jnario. All of the possibilities are
        * described [here](/org/jnario/spec/tests/integration/AssertionSpec.html).
        * 
        * For describing the expected result of an expression `=>` is used.
        * 
        * @filter('''|.executesSuccessfully)
        * @lang(feature)
        */
       fact "Behavior assertions"{
         '''
       package demo
       import tutorialtest.math.Calculator
       
       Feature: Calculator
       
       Scenario: Adding two numbers
         Calculator calculator
         int result
         Given a calculator
           calculator = new Calculator
         When I enter two numbers and press add
           result = calculator.add(20,70)
         Then it should show the expected result
           result => 90
         '''.executesSuccessfully
       }
       
       /*
        * **Given** steps that are used in all scenarios of the same feature can be defined in a `Background`.
        * All steps defined as background steps will be executed for each scenario before the steps from
        * the individual scenarios are executed.
        * 
        * @filter('''|.executesSuccessfully)
        * @lang(feature)
        */
       fact "Background"{
         '''
       package demo
       import tutorialtest.math.Calculator
       
       Feature: Calculator
       
       Background:
         Calculator calculator
         Given a calculator
       
       Scenario: Adding two numbers
         int result
         When I enter two numbers and press add
           result = calculator.add(20,70)
         Then it should show the expected result
           result => 90
         
       Scenario: Dividing two numbers
        When I enter the number 70
        And I enter the number 10
        And I press divide
        Then it should show 7
         '''.executesSuccessfully
       }
      
      /*
       * Often you want to verify the behavior of a scenario with multiple examples.
       * In case of the calculator you might want to define what happens using negative numbers
       * or large numbers. In this case you can define a table with values that can be used
       * by the scenario steps. Each scenario will be executed separately for each row in the table.
       * 
       * The header of the example table serves as field declaration of the header names. The
       * type is derived from the common super type of a column.
       * 
       * @filter('''|.executesSuccessfully)
       * @lang(feature)
       */ 
      fact "Example tables"{
         '''
       package demo
       import tutorialtest.math.Calculator
       
       Feature: Calculator
       
       Scenario: Adding two numbers
         Calculator calculator
         int result
         Given a calculator
           calculator = new Calculator
         When I enter two numbers and press add
           result = calculator.add(a,b)
         Then it should show the expected result
           result => expectedResult
           
         Examples: Possible addition values
         | a    | b    | expectedResult  |
         | 0    | 0    | 0          |
         | 21  | 21  | 42        |
         | -3  | -5  | -8        |
         '''.executesSuccessfully
       }
       
       /*
        * Once a step is defined including the execution code it can be referenced from
        * other scenarios. The textual description of the step serves as identifier.
        * When you reuse the step in a different scenario and provide no implementation
        * the existing implementation from the referenced step will be used for this step.
        * 
        * Referenced steps are highlighted in grey so you see when you are actually using
        * a referenced step. You can also jump directly to the original step declaration
        * to have a look at the implementation.
        * 
        * @filter('''|.executesSuccessfully)
        * @lang(feature)
        */
       fact "Step references"{
         '''
       package demo
       import tutorialtest.math.Calculator
       
       Feature: Calculator
       
       Background:
         Calculator calculator
         Given a calculator
           calculator = new Calculator
       
       Scenario: Adding two numbers
         int result
         When I enter two numbers and press add
           result = calculator.add(a,b)
         Then it should show the expected result
           result => expected_result
           
         Examples:
         | a    | b    | expected_result  |
         | 0    | 0    | 0          |
         | -3  | -5  | -8        |
         
       Scenario: Dividing two numbers
         double result
        When I enter two numbers and press divide
          result = calculator.divide(a,b)
        Then it should show the expected result
        
        Examples:
        | a    | b    | expected_result  |
        | 8    | 1    | 8                |
         '''.executesSuccessfully
       }
   }   
  
}