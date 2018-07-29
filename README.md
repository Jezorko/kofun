# kofun

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Build Status](https://travis-ci.org/Jezorko/kofun.svg?branch=master)](https://travis-ci.org/Jezorko/kofun)
[![Maintainability](https://api.codeclimate.com/v1/badges/f1ec4528ddab4eccb759/maintainability)](https://codeclimate.com/github/Jezorko/kofun/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/f1ec4528ddab4eccb759/test_coverage)](https://codeclimate.com/github/Jezorko/kofun/test_coverage)

## Implementation progress

[![Implementation progress](https://docs.google.com/spreadsheets/d/e/2PACX-1vSerK7xz_n-Nz67OtpLhm0ea4Kdi6v77lkq3Twu1dPwc9d8PoDB6kLklFZqRu3qATL9s65tNVzCmrQh/pubchart?oid=164078757&format=image)](https://docs.google.com/spreadsheets/d/1hhDRsxyS6Aqb-4e1aFYqDN4i_Zc7SuW3QgC2R1X25yM/edit?usp=sharing)

## What is this?

This is an attempt at making an extensible, packed with features and neatly documented functional Java library.

It's based on two concepts: fluent prototypes and their extensions.

An example of these concepts in action can be found [in this issue](https://github.com/Jezorko/kofun/issues/8).

### Fluent prototype

A "fluent prototype" is an interface crafted specifically with intent of [chaining method calls](https://en.wikipedia.org/wiki/Method_chaining).

Each prototype must define a generic type with itself as lower bound.
This type adds type information to extending classes so that the Java compiler knows about any new methods added to the extension class.

Methods that can be chained are marked with `@ExtensibleFluentChain` in the prototype.
All those methods must be implemented by the extending class.
`ExtensibleFluentChainTestUtil` can be used to make sure everything is implemented correctly.

### Prototype extension

It is an implementation of a `FluentPrototype` that overwrites all types in `@ExtensibleFluentChain` methods.
Here, new methods can be added to extend prototype's capabilities.

## Why would I want to use it?

Most functional libraries (including java.util classes) often provide monads as final classes.
Extending such a class is impossible and so a multitude of "utility" classes are born to add missing functionalities.

Creating a new class for each monad is tedious and makes them incompatible with other implementations.

The goal of this library is to:
 1. make monads extensible
 2. be compatible with other functional libraries
 3. provide tools for most use cases
 
 which can be shortened to a simple motto, "take less, give more".
 
## How can I add it to my project?

Currently this library is in rather early stage of development.
There's only one version, `EARLY-SNAPSHOT` which can change dramatically over time for whatever reason.

If your code works today, it may break tomorrow.

### Maven

Add repository to your pom.xml file:

```xml
<repositories>
    <repository>
        <id>jezorko-kofun</id>
        <name>jezorko-kofun</name>
        <url>https://raw.githubusercontent.com/jezorko/kofun/repository</url>
    </repository>
</repositories>
```

And the dependency:

```xml
<dependency>
    <groupId>io.kofun</groupId>
    <artifactId>kofun</artifactId>
    <version>EARLY-SNAPSHOT</version>
</dependency>
```