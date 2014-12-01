## User guide

Joda-Convert is intended for one simple task -
Converting objects to and from strings.
This is a common problem, particularly when communicating over textual protocols like XML or JSON.

## Basic usage

Using Joda-Convert is easy at the simplest level.
The main access is via the class `StringConvert`.

The easiest way to use the conversion is via the global constant:

```
// conversion to a String
TimeZone zone = ...
String str = StringConvert.INSTANCE.convertToString(zone);

// conversion from a String
TimeZone zone = StringConvert.INSTANCE.convertFromString(TimeZone.class, str);
```

In both cases, if the input is `null` then the output will also be `null`.

The global constant is quick and easy to use, but is shared between all users in the `ClassLoader`.
It also cannot be extended.

The alternative approach is to instantiate your own instance of `StringConvert`.
This would normally be stored in your own static variable, or made available as needed by dependency injection.
This may be updated by registering your own converters.


## Converters

Each instance of `StringConvert`, including the global singleton, includes a standard set of JDK-based converters.
These cover all the standard JDK types for which conversion to and from a string is sensible.
The set also includes JSR-310 types, but these are optional and loaded by reflection.
The system will run without any dependency.

The JDK conversions are generally obvious. The types are as follows:

* String
* CharSequence
* StringBuffer
* StringBuilder
* long and Long
* int and Integer
* short and Short
* char and Character
* byte and Byte
* double and Double
* float and Float
* boolean and Boolean - 'true' or 'false'
* byte[] - using Base-64 encoding
* char[]
* BigInteger
* BigDecimal
* AtomicLong
* AtomicInteger
* AtomicBoolean - 'true' or 'false'
* Locale - separated by underscores, en_GB_VARIANT
* Class - using the class name, using the rename handler
* Package - using the package name
* Currency - using the three letter code
* TimeZone - using the ID
* UUID - using the toString() form
* URL - using the toString() form
* URI - using the toString() form
* InetAddress - using the host address
* File - using the toString() form
* Date - yyyy-MM-dd'T'HH:mm:ss.SSSZ
* Calendar - yyyy-MM-dd'T'HH:mm:ss.SSSZ, Gregorian only
* Instant
* Duration
* LocalDate
* LocalTime
* LocalDateTime
* OffsetTime
* OffsetDateTime
* ZonedDateTime
* Year
* YearMonth
* MonthDay
* Period
* ZoneOffset
* ZoneId
* Enum subclasses - using name(), annotations can override

Note that the JSR-310 date types are supported in three different package namespaces -
'java.time', 'javax.time' and 'org.threeten.bp'.

Each `StringConvert` instance, other than the global singleton, may have additional converters registered manually.
Each converter implements the `StringConverter` interface, which is self explanatory.

Converters may also be manually added by method name.
This is equivalent to using annotations, but suitable when you don't own the code to add them.
See `StringConvert.registerMethods` and `StringConvert.registerMethodConstructor`.


## Factories

In addition to manual registration of individual converters, each instance of `StringConvert`
has a list of factories to use. The `StringConverterFactory` interface defines the factory.
This allows either bulk registration or dynamic lookup of converters.

A factory is provided to allow numeric arrays to be converted to/from a comma separated list.
A separate factory handles numeric object arrays.
Another factory is provided to allow boolean arrays to be converted to/from a string such as 'TTFFT'.
Again, a separate factory handles boolean object arrays.
Primitive byte and char arrays are handled by default, but the primitive object arrays are
handled via their own factories.

These extra factories must be manually registered, unless the `StringConvert.create()`
static method is used, which defines an "extended" converter with the factories included.


## Annotation based conversion

If there is no registered converter for a type, then a search by annotation is performed.
This will search for the `ToString` and `FromString` annotation on the type.
These annotations will indicate which method should be called to perform the conversion.

```
public class Distance {

  @FromString
  public static Distance parse(String str) { ... }

  @ToString
  public String getStandardOutput() { ... }

}
```

To be valid, the class must contain one `ToString` annotation and one `FromString` annotation.
The `ToString` annotation must be an instance method taking no parameters and returning a String.
The `FromString` annotation must be either a static method or a constructor taking a String parameter and
returning the correct type.
If the annotations are not found on the target class, then superclasses are searched, followed by immediate parent interfaces.

Sometimes, you want to provide to/from string conversions for interfaces.
In Java SE 8 this can be done using static methods on interfaces.
However in earlier versions, a separate "factory" class is necessary.
This can also be annotated:

```
@FromStringFactory(factory = DistanceFactory.class)
public interface Distance {

  @ToString
  String standardFormat();

}

public class Metres implements Distance {

  @Override
  public String standardFormat() { ... }

}

public class DistanceFactory {

  @FromString
  public static Distance parseDistance(String str) { ... }

}
```

The `FromStringFactory` annotation points at the factory class that will provide the factory method.
Although intended for use with interfaces, it can also be used on the target class or any superclass.
Note that only the immediate parent interfaces of a class will be searched.

The effective type of the converter in use is the type that declares the `FromString`
or `FromStringFactory` annotation.
This can be used by serialization systems to determine the best type of the value to send.
One use case is to declare annotations on a public superclass and have all the subclasses be package scoped.
Using the effective type, the package scoped subclasses remain out of the serialized form and
off the public API.


## Rename handler

Most large bodies of code will end up renaming classes and enum constants at some point.
The `RenameHandler` class provides a convenient central place to track this.

If the `RenameHandler` is setup with details of a rename, then an old class name
or enum constant can be read in and will be automatically converted.


## Rationale

The concept is that other open source libraries, as well as your application code, will implement these two annotations.
For open source projects, a key point is that adding the annotations is a compile-time only event.
The Joda-Convert jar file is not needed by your users unless they want to use conversion.
If they don't want to use Joda-Convert then the annotations are effectively ignored.

Joda-Time v2.0 and Joda-Money contain these annotations.
In both cases, the dependency is optional at runtime for users of the projects.
(Note that Scala does not honour the optional behaviour.)
