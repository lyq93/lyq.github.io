[toc]

## 自定义校验器

### 步骤

- 创建一个需要校验的对象
- 创建一个注解类
- 创建一个校验器

### 代码

- 被校验对象

  ```java
  package com.sz.lyq.validationDemo.domain;
  
  import com.sz.lyq.validationDemo.validation.constraints.PersonNamePrefix;
  
  import javax.validation.constraints.Max;
  import javax.validation.constraints.Min;
  import javax.validation.constraints.NotNull;
  
  public class Person {
      @PersonNamePrefix(prefix = "sz_")
      public String name;
      @Min(value = 0)
      @Max(value = 200, message = "{person.age.max.message}")
      public int age;
  
      public String getName() {
          return name;
      }
  
      public void setName(String name) {
          this.name = name;
      }
  
      public int getAge() {
          return age;
      }
  
      public void setAge(int age) {
          this.age = age;
      }
  }
  ```

  >  在Person类上使用@PersonNamePrefix对名字的前缀进行校验

- 注解类

  ```java
  package com.sz.lyq.validationDemo.validation.constraints;
  
  import javax.validation.Constraint;
  import javax.validation.Payload;
  import javax.validation.constraints.Max;
  import java.lang.annotation.*;
  
  @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
  @Retention(RetentionPolicy.RUNTIME)
  //@Repeatable(Max.List.class)
  @Documented
  @Constraint(
          validatedBy = {PersonNamePrefixConstraintValidator.class}
  )
  public @interface PersonNamePrefix {
      String message() default "{person.name.prefix.message}";
  
      Class<?>[] groups() default {};
  
      Class<? extends Payload>[] payload() default {};
  
      String prefix() default  "sz-";
  }
  ```

  > @Constraint注解指明这个注解类所使用的校验器是PersonNamePrefixConstraintValidator

- 自定义校验器

  ```java
  package com.sz.lyq.validationDemo.validation.constraints;
  
  import com.sz.lyq.validationDemo.domain.Person;
  
  import javax.validation.ConstraintValidator;
  import javax.validation.ConstraintValidatorContext;
  
  public class PersonNamePrefixConstraintValidator implements ConstraintValidator<PersonNamePrefix,String> {
      private String personNamePrefix;
  
      @Override
      public void initialize(PersonNamePrefix constraintAnnotation) {
          this.personNamePrefix = constraintAnnotation.prefix();
      }
  
      @Override
      public boolean isValid(String personName, ConstraintValidatorContext constraintValidatorContext) {
          if(!personName.startsWith(personNamePrefix)) {
              return false;
          }
          return true;
      }
  }
  ```

  > 校验器需要实现ConstraintValidator接口，把自定义的注解类传入该接口