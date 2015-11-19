# datamill [![Build Status](https://travis-ci.org/rchodava/datamill.svg?branch=master)](https://travis-ci.org/rchodava/datamill) [![Coverage Status](https://coveralls.io/repos/rchodava/datamill/badge.svg?branch=master&service=github)](https://coveralls.io/github/rchodava/datamill?branch=master)
## Introduction

datamill is a Java framework for web applications using a functional reactive style built on RxJava. It is intended to
be used with Java 8 and lambdas. Unlike other modern Java frameworks, it makes the flow and manipulation of data through
your application highly visible. That means that you won't find yourself strewing your code with magic annotations,
whose function and effect are hidden within complex framework code and documentation. Instead, you will explicitly
specify how data flows through your application, and how to modify that data as it does. And you do so using the simple
style that RxJava allows.

To give a sense of how this looks, we will start with an example that uses a lot of elements within the framework to build
a simple service that returns a JSON response after querying a relational database:

```java
public class Main {
    public void main(String[] args) throws Exception {
        DatabaseClient dbClient = new DatabaseClient("jdbc:mysql://localhost:3306/exampledb", "user", "pass");
        dbClient.migrate();

        OutlineBuilder outlineBuilder = new OutlineBuilder();
        Outline<User> outline = outlineBuilder.defaultSnakeCased().build(User.class);

        Server server = new Server(rb ->
            rb.ifMethodAndUriMatch(Method.GET, "/users/{id}", request ->
                dbClient.select(outline.propertyNames()).from(outline.pluralName()).where()
                    .eq(outline.name(outline.members().getId()), request.uriParameter("id").asLong())
                .map(row -> 
                    new JsonObject()
                        .put(
                            outline.name(outline.members().getId()),
                            row.column(outline.name(outline.members().getId())))
                        .put(
                            outline.name(outline.members().getEmail()),
                            row.column(outline.name(outline.members().getEmail())))
                        .put(
                            outline.name(outline.members().getLocation()),
                            row.column(outline.name(outline.members().getLocation())))
                        .put(
                            outline.name(outline.members().getName()),
                            row.column(outline.name(outline.members().getName()))));                        
                .flatMap(json -> request.respond().ok(json))
                .switchIfEmpty(request.respond().notFound())
            .orElse(r -> r.respond().notFound()));

        server.listen(8080);
    }
```

The example serves to illustrate the style of code you will write - in order to analyze it, we will first discuss the 
various concepts being used. To begin, we will take a look at some of the basic building blocks in the framework, 
starting with the utilities for reflection.

## Reflection

One of the primary utilities in datamill is an API for performing reflection on your Java classes. A core concept in 
this reflection API is an outline. An outline provides an easy way for you to get the names of the various properties 
and methods of your classes. For example, let's build an outline for a simple entity:

```java
import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;

public class Main {
    public class SystemUser {
        private String firstName;

        public String getFirstName() { return firstName; }
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

The `name` methods return snake_cased names. This is because we built an outline, calling `defaultSnakeCased()` on the 
builder. We could have defaulted to having camelCased names by calling `defaultcamelCased()` on the builder instead. The
outline also has specific methods for obtaining camelCased, snake_cased, and pluralized names (using an English 
inflector library).

Take a closer look at the way property names are obtained. Calling the `members()` method on an outline returns a 
special proxy instance of the entity being outlined. In this case, we have a special proxy instance of `SystemUser`. 
Calling the `getFirstName` getter on this proxy instance does not return anything meaningful. Instead, when you make the
getter call, a record is kept of the getter call. When the `name` method is subsequently called on the outline, we take
a look at the record of the call we made to the outline proxy's getter method. The `name` method returns the name of the
property whose getter or setter was last invoked. This is the reason why 
`userOutline.name(userOutline.members().getFirstName())` returns "first_name".

Note that this mechanism is thread-safe so that an outline can be safely re-used in multiple threads and still return 
the correct name. Note also that this mechanism means that we are not resorting to using strings.

Here is another example showing outlines at work:

```java
import org.chodavarapu.datamill.reflection.Outline;
import org.chodavarapu.datamill.reflection.OutlineBuilder;

public class Main {
    public class NewsStream {
        private String title;
        private String description;
        private String imageUrl;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getImageUrl() { return description; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static void main(String[] arguments) {
        Outline<NewsStream> streamOutline = new OutlineBuilder(NewsStream.class).defaultCamelCased().build();
        
        String entityName = streamOutline.name(); // returns "NewsStream"
        String titleName = streamOutline.name(streamOutline.members().getTitle()); // returns "title"
        String imageUrlName = streamOutline.name(streamOutline.members().getTitle()); // returns "imageUrl"
        String descriptionName = streamOutline.name(members -> members.setTitle("")); // returns "description"
    }
}
```

## Values

Another set of utilities in datamill is a fluent API for dealing with values of various types. A primary feature offered
by this API is the ability to easily cast between the types. Consider for example HTTP request parameters:

```java
public class Main {
    public void main(String[] args) throws Exception {
        server = new Server(rb ->
            rb.ifMethodAndUriMatch(Method.GET, "/users/{id}", request ->
                request.uriParameter("id").asLong() == 0 ? 
                    request.respond().ok() : 
                    request.respond().notFound()));
    }
}
```

Here, the request parameter "id", retrieved using `request.uriParameter("id")` returns a `Value`. This URI parameter, as
with all URI parameters, is originally a string value. But notice that it is easily converted into a long value using
the `asLong()` method. There is a method to quickly cast to all the primitive types. Another example of where values 
are returned is when you access database row data:

```java
public class Main {
    public void main(String[] args) throws Exception {
        DatabaseClient dbClient = new DatabaseClient("jdbc:mysql://localhost:3306/exampledb", "user", "pass");
        dbClient.migrate();

        OutlineBuilder outlineBuilder = new OutlineBuilder();
        Outline<User> outline = outlineBuilder.defaultSnakeCased().build(User.class);

        Server server = new Server(rb ->
            rb.ifMethodAndUriMatch(Method.GET, "/users/{id}/active", request ->
                dbClient.select(outline.propertyNames()).from(outline.pluralName()).where()
                    .eq(outline.name(outline.members().getId()), request.uriParameter("id").asLong())
                .map(row -> row.column(outline.name(outline.members().isActive())).asBoolean())
                .flatMap(active -> active ? 
                    r.respond.ok("Active") :
                    r.respond.ok("Not Active"))));
    }
}
```

Note that when we retrieve the value of the "active" column using `row.column(outline.name(outline.members().isActive()))`,
we get back a value, which we cast to a boolean with a call to `asBoolean()`. In any location where the ability to perform
this easy casting is appropriate, `Value`s are used within the framework.
