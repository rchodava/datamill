# datamill [![Build Status](https://travis-ci.org/rchodava/datamill.svg?branch=master)](https://travis-ci.org/rchodava/datamill)

## Introduction

datamill is a Java framework for data-driven web applications with a focus on making it easy to follow data as it flows through your application. It is a reaction and an alternative to black magic Java frameworks which make it impossible to understand your application logic without wading through complex framework code and documentation for hours.

## Reflection

One of the primary utilities in datamill is an API for performing reflection on your Java classes. A core concept in this reflection API is an outline. An outline provides an easy way for you to get the names of the various properties and methods of your classes. For example, let's build an outline for a simple entity:

```java
import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;

public class Main {
    public class SystemUser {
        private String firstName;

        public String getFirstName() {
            return firstName;
        }
    }

    public static void main(String[] arguments) {
        Outline<SystemUser> userOutline = new OutlineBuilder(SystemUser.class).defaultSnakeCased().build();
    }
}
```

Using this outline, we can now get the name of the class itself, as well as the property:

```java
String entityName = userOutline.name(); // returns "system_user"
String propertyName = userOutline.name(userOutline.members().getFirstName()); // returns "first_name"
```

Note that the `name` methods return snake_cased names. This is because we built an outline, calling `defaultSnakeCased()` on the builder. We could have defaulted to using camelCased names by calling `defaultcamelCased()` instead. The outline also has specific methods for obtaining camelCased, snake_cased, and pluralized names (using an English inflector library).

Note specifically the way property names are obtained. Calling the `members()` method on an `Outline` returns a special proxy instance of the entity class. Calling the `getFirstName` getter on this proxy instance records a call on this particular getter so that when the `name` method is called on the outlin`, it will return the last call made to the outline proxy's methods. Note that this mechanism is thread-safe so that an outline can be safely re-used in multiple threads and still return the correct name.
