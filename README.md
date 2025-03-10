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
The 3.x branch is compatible with Java SE 21 or later.

The 2.x branch is compatible with Java SE 6 or later.

v2.x releases are compatible with v1.x releases, with the exception that the direct Guava dependency is removed.
v3.x releases are compatible with v2.x releases, however the `module-info.class` file is always present,
and the oldest development pre-release of JSR-310 is not recognized.

Joda-Convert has [no dependencies](dependencies.html).

Available in the [Maven Central repository](https://search.maven.org/search?q=g:org.joda%20AND%20a:joda-convert&core=gav).
[GitHub release bundles](https://github.com/JodaOrg/joda-convert/releases).

![Tidelift dependency check](https://tidelift.com/badges/github/JodaOrg/joda-convert)


### For enterprise
Available as part of the Tidelift Subscription.

Joda and the maintainers of thousands of other packages are working with Tidelift to deliver one enterprise subscription that covers all of the open source you use.

If you want the flexibility of open source and the confidence of commercial-grade software, this is for you.

[Learn more](https://tidelift.com/subscription/pkg/maven-org-joda-joda-convert?utm_source=maven-org-joda-joda-convert&utm_medium=github)


### Support
Please use [Stack Overflow](https://stackoverflow.com/search?q=joda-convert) for general usage questions.
GitHub [issues](https://github.com/JodaOrg/joda-convert/issues) and [pull requests](https://github.com/JodaOrg/joda-convert/pulls)
should be used when you want to help advance the project.

Any donations to support the project are accepted via [OpenCollective](https://opencollective.com/joda).

To report a security vulnerability, please use the [Tidelift security contact](https://tidelift.com/security).
Tidelift will coordinate the fix and disclosure.


### Release process

* Update version (index.md, changes.xml)
* Commit and push
* `git push origin HEAD:refs/tags/release`
* Code and Website will be built and released by GitHub Actions

Release from local:

* Turn off gpg "bc" signer
* `mvn clean release:clean release:prepare release:perform`
