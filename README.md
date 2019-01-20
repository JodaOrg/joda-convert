Joda-Convert
------------

Joda-Convert is a small, highly-focussed [library](https://www.joda.org/joda-convert/apidocs/org.joda.convert/org/joda/convert/StringConvert.html), tackling a problem that the JDK should solve -
providing round-trip conversion between Objects and Strings.
It is not intended to tackle the wider problem of Object to Object transformation.

```java
// conversion to String
String str = StringConvert.INSTANCE.convertToString(foo);

// conversion from String
Foo bar = StringConvert.INSTANCE.convertFromString(Foo.class, str);
```

Joda-Convert supports two mechanisms of extending the list of supported conversions.
The first is to write your own [converter](https://www.joda.org/joda-convert/apidocs/org.joda.convert/org/joda/convert/TypedStringConverter.html) implementing an interface.
The second is to use annotations.

The ability of Joda-Convert to use annotations to define the conversion methods is a key difference from other projects.
For example, most value classes, like `Currency` or `TimeZone`, already have methods
to convert to and from a standard format String.
Consider a `Distance` class annotated with
[`FromString`](https://www.joda.org/joda-convert/apidocs/org.joda.convert/org/joda/convert/FromString.html) and
[`ToString`](https://www.joda.org/joda-convert/apidocs/org.joda.convert/org/joda/convert/ToString.html):

```java
public class Distance {

  @FromString
  public static Distance parse(String str) { ... }

  @ToString
  public String getStandardOutput() { ... }

}
```

As shown, the two methods may have any name. They must simply fulfil the required method signatures for conversion.
The `FromString` annotation may also be applied to a constructor.

When Joda-Convert is asked to convert between an object and a String, if there is no registered converter
then the annotations are checked. If they are found, then the methods are called by reflection.

Joda-Convert is licensed under the business-friendly [Apache 2.0 licence](https://www.joda.org/joda-convert/licenses.html).


### Documentation
Various documentation is available:

* The [home page](https://www.joda.org/joda-convert/)
* The helpful [user guide](https://www.joda.org/joda-convert/userguide.html)
* The [Javadoc](https://www.joda.org/joda-convert/apidocs/index.html)
* The change notes for the [releases](https://www.joda.org/joda-convert/changes-report.html)


### Releases
[Release 2.2.0](https://www.joda.org/joda-convert/download.html) is the current latest release.
This release is considered stable and worthy of the 2.x tag.
The v2.x releases are compatible with v1.x releases, with the exception that the direct Guava dependency is removed.
It depends on Java SE 6 or later.

Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.joda%20AND%20a:joda-convert&core=gav)

![Tidelift dependency check](https://tidelift.com/badges/github/JodaOrg/joda-beans)


### Support
Please use [Stack Overflow](https://stackoverflow.com/search?q=joda-convert) for general usage questions.
GitHub [issues](https://github.com/JodaOrg/joda-convert/issues) and [pull requests](https://github.com/JodaOrg/joda-convert/pulls)
should be used when you want to help advance the project.
Commercial support is available via the
[Tidelift subscription](https://tidelift.com/subscription/pkg/maven-org-joda-joda-convert?utm_source=maven-org-joda-joda-convert&utm_medium=referral&utm_campaign=readme).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.


### Release process

* Update version (README.md, index.md, changes.xml)
* Commit and push
* `mvn clean release:clean release:prepare release:perform`
* `git fetch`
* Website will be built and released by Travis
