# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

toml4j is a [TOML 0.4.0](https://github.com/toml-lang/toml/blob/master/versions/en/toml-v0.4.0.md) parser and writer for Java, published to Maven Central as `de.thelooter:toml4j`. It is a fork of the original [mwanji/toml4j](https://github.com/mwanji/toml4j). The only runtime dependency is Gson, used solely to map parsed data onto user classes.

## Build & Test

The project uses **Gradle** (via the `./gradlew` wrapper), not Maven. Ignore the `mvn` commands in `CONTRIBUTING.md` — they are stale from the upstream fork.

- Build: `./gradlew build`
- Run all tests: `./gradlew test`
- Run a single test class: `./gradlew test --tests "de.thelooter.toml.NumberTest"`
- Run a single test method: `./gradlew test --tests "de.thelooter.toml.NumberTest.should_get_long"`
- Coverage report (JaCoCo): `./gradlew jacocoTestReport` → output in `build/jacocoHtml/index.html`. `check` runs it automatically.

Tests use JUnit 5 (Jupiter) + Hamcrest. Source/target compatibility is Java 11, though the README advertises Java 8+ for consumers. The published artifact version comes from the `TOML4J_VERSION` env var (suffixed `-dev` outside CI); see `createVersion()` in `build.gradle.kts`.

## Architecture

Everything lives in the single package `de.thelooter.toml` (`src/main/java/de/thelooter/toml/`). The public API surface is just two classes — `Toml` (read/query) and `TomlWriter` (serialize). The module (`module-info.java`) exports only this package.

### Reading: hand-written parser + strategy chain

There is **no ANTLR/generated grammar** — parsing is a hand-written character scanner.

1. `Toml.read(...)` feeds a String to `TomlParser.run()`, which scans char-by-char, tracking line numbers and comment state.
2. Identifiers (keys, `[tables]`, `[[table arrays]]`) are recognized by `IdentifierConverter` → `Identifier`.
3. Values are dispatched through `ValueReaders.VALUE_READERS`, a **chain-of-responsibility**: it tries each `ValueReader` in a fixed-order array, calling `canRead()` then `read()`. Order matters (e.g. multiline/literal string readers must precede the plain string reader). To support a new value syntax, add a `ValueReader` and insert it in the `READERS` array in `ValueReaders`.
4. `Results` accumulates the parsed structure into nested `Container.Table` / `Container.TableArray` objects and collects `Results.Errors`. Any parse error makes `read()` throw `IllegalStateException` with the accumulated messages.
5. The final flat map of values backs the `Toml` instance.

### Querying

- Compound keys like `table.key` and `tableArray[0].key` are split/navigated via `Keys`.
- Getters return `null` for missing primitives; `getList`/`getTable`/`getTables` return empty.
- **Defaults**: a `Toml` can wrap another `Toml` (constructor arg) as fallback. Constructor defaults take precedence over per-getter defaults — see `DefaultValueTest`.
- `Toml.to(Class<T>)` does **not** map fields directly. It converts the value map to a Gson `JsonTree` via `toMap()`, then `Gson.fromJson(...)` onto the target class. This is why arbitrary nesting of custom classes, maps, and collections "just works."

### Writing

Mirror image of reading. `TomlWriter.write(...)` dispatches each object through `ValueWriters.WRITERS.findWriterFor()` (same strategy pattern, falling back to `ObjectValueWriter` for arbitrary beans). Formatting (indentation, array padding) is controlled by `TomlWriter.Builder` → `IndentationPolicy`, threaded through `WriterContext`. `TomlWriter` is immutable and threadsafe.

### Shared reader/writers

Several types implement both directions in one class, named `*ValueReaderWriter` (e.g. `StringValueReaderWriter`, `NumberValueReaderWriter`, `BooleanValueReaderWriter`, `DateValueReaderWriter`) and are registered as singletons in both `ValueReaders` and `ValueWriters`. When changing one direction of such a type, check whether the other direction needs the matching change.

## Tests

Conformance is validated against the **BurntSushi** TOML test suite: `BurntSushiValidTest`, `BurntSushiInvalidTest`, `BurntSushiValidEncoderTest` read fixtures from `src/test/resources/de/thelooter/toml/burntsushi/{valid,invalid}/`. When adding parser features or fixing bugs, prefer adding a fixture there plus a focused test class. Conversion-to-class tests use helper beans in `src/test/java/de/thelooter/toml/testutils/`.

## Conventions (from CONTRIBUTING.md)

- 2-space indentation; opening braces on the same line.
- Cover new/modified functionality with tests and update the UNRELEASED section of `CHANGELOG.md` (keepachangelog format).
- PRs target the `wip` branch; `master` only changes on release.
