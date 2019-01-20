## <i></i> About

**Joda-Convert** tackles the problem of round-trip Object to String conversion.

A common problem in serialization, particularly when working with textual formats
like JSON, is that of converting simple objects to and from strings.
Joda-Convert addresses this without getting caught up in the wider problem
of Object to Object transformation.

Joda-Convert is licensed under the business-friendly [Apache 2.0 licence](licenses.html).


## <i></i> Features

A selection of key features:

* Round-trip Object to String
* Conversions defined by annotations or by implementing a simple interface
* Very simple to use
* No dependencies


## <i></i> Documentation

Various documentation is available:

* The helpful [user guide](userguide.html)
* The [Javadoc](apidocs/index.html)
* The [change notes](changes-report.html) for each release
* The [GitHub](https://github.com/JodaOrg/joda-convert) source repository


---

## <i></i> Why Joda Convert?

Joda-Convert is a small, highly-focussed library, tackling a problem that the JDK should solve -
providing round-trip conversion between Objects and Strings.

<pre>
  // conversion to String
  <b>String str = <a href="apidocs/org.joda.convert/org/joda/convert/StringConvert.html">StringConvert</a>.INSTANCE.convertToString(foo);</b>

  // conversion from String
  <b>Foo bar = StringConvert.INSTANCE.convertFromString(Foo.class, str);</b>
</pre>

Joda-Convert supports two mechanisms of extending the list of supported conversions.
The first is to write your own [converter](apidocs/org.joda.convert/org/joda/convert/TypedStringConverter.html) implementing an interface.
The second is to use annotations.

The ability of Joda-Convert to use annotations to define the conversion methods is a key difference from other projects.
For example, most value classes, like `Currency` or `TimeZone`, already have methods
to convert to and from a standard format String.
Consider a `Distance` class:

<pre>
  <b>public class Distance {</b>
    <b><a href="apidocs/org.joda.convert/org/joda/convert/FromString.html">@FromString</a></b>
    <b>public static Distance parse(String str) { ... }</b>

    <b><a href="apidocs/org.joda.convert/org/joda/convert/ToString.html">@ToString</a></b>
    <b>public String getStandardOutput() { ... }</b>
  <b>}</b>
</pre>

As shown, the two methods may have any name. They must simply fulfil the required method signatures for conversion.
The `FromString` annotation may also be applied to a constructor.

When Joda-Convert is asked to convert between an object and a String, if there is no registered converter
then the annotations are checked. If they are found, then the methods are called by reflection.


---

## <i></i> Releases

[Release 2.2.0](download.html) is the current latest release.
This release is considered stable and worthy of the 2.x tag.
The v2.x releases are compatible with v1.x releases, with the exception that the direct Guava dependency is removed.

Joda-Convert requires Java SE 6 or later and has [no dependencies](dependencies.html).

Available in [Maven Central](https://search.maven.org/search?q=g:org.joda%20AND%20a:joda-convert&core=gav).

```xml
<dependency>
  <groupId>org.joda</groupId>
  <artifactId>joda-convert</artifactId>
  <version>2.2.0</version>
</dependency>
```

The main jar file is based on Java 6 but contains a `module-info.class` file for Java 9 and later.
If you have problems with this, there is a "classic" variant you can use instead:

```xml
<dependency>
  <groupId>org.joda</groupId>
  <artifactId>joda-convert</artifactId>
  <version>2.2.0</version>
  <classifier>classic</classifier>
</dependency>
```

---

### Support

Please use [Stack Overflow](https://stackoverflow.com/search?q=joda-convert) for general usage questions.
GitHub [issues](https://github.com/JodaOrg/joda-convert/issues) and [pull requests](https://github.com/JodaOrg/joda-convert/pulls)
should be used when you want to help advance the project.
Commercial support is available via the
[Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-joda-joda-convert?utm_source=maven-org-joda-joda-convert&utm_medium=referral&utm_campaign=website).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.
