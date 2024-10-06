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

The 3.x branch (v3.0.1) is compatible with Java SE 21 or later.

The 2.x branch (v2.2.4) is compatible with Java SE 6 or later.

v3.x releases are compatible with v2.x releases, however the `module-info.class` file is always present,
and the oldest development pre-release of JSR-310 is not recognized.
v2.x releases are compatible with v1.x releases, with the exception that the direct Guava dependency is removed.

Joda-Convert has no mandatory dependencies, however Guava and ThreeTen-Backport will be used if present.

Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.joda%20AND%20a:joda-convert&core=gav).
[GitHub release bundles](https://github.com/JodaOrg/joda-convert/releases).

```xml
<dependency>
  <groupId>org.joda</groupId>
  <artifactId>joda-convert</artifactId>
  <version>3.0.1</version>
</dependency>
```

Java module name: `org.joda.convert`.

---

### For Enterprise

[Available as part of the Tidelift Subscription](https://tidelift.com/subscription/pkg/maven-org-joda-joda-money?utm_source=maven-org-joda-joda-money&utm_medium=referral&utm_campaign=enterprise).

Joda and the maintainers of thousands of other packages are working with Tidelift to deliver one
enterprise subscription that covers all of the open source you use.

If you want the flexibility of open source and the confidence of commercial-grade software, this is for you.
[Learn more](https://tidelift.com/subscription/pkg/maven-org-joda-joda-convert?utm_source=maven-org-joda-joda-convert&utm_medium=referral&utm_campaign=enterprise).


### Support

Please use [Stack Overflow](https://stackoverflow.com/search?q=joda-convert) for general usage questions.
GitHub [issues](https://github.com/JodaOrg/joda-convert/issues) and [pull requests](https://github.com/JodaOrg/joda-convert/pulls)
should be used when you want to help advance the project.

Any donations to support the project are accepted via [OpenCollective](https://opencollective.com/joda).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.
