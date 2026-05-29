Thank you for taking the time to contribute to toml4j! Here are a few guidelines to streamline the process.

* Pull Requests should be opened against the wip branch. Master changes only when there is a new release.
* Cover new or modified functionality with unit tests. Run `./gradlew test jacocoTestReport` and look at `build/jacocoHtml/index.html` to view code coverage.
* Amend README.md as necessary
* Update the UNRELEASED section of CHANGELOG.md, as described in [keepachangelog.com](http://keepachangelog.com)
* Use 2 spaces for indentation
* Opening braces, parentheses, etc. are not on a new line

## Dependencies

Dependency versions live in the [version catalog](gradle/libs.versions.toml). The build also uses
[dependency locking](https://docs.gradle.org/current/userguide/dependency_locking.html)
(`gradle.lockfile`, `settings-gradle.lockfile`) and
[dependency verification](https://docs.gradle.org/current/userguide/dependency_verification.html)
with PGP signatures plus SHA-256 fallback (`gradle/verification-metadata.xml`, and the trusted public
keys in `gradle/verification-keyring.keys`). So whenever you change a dependency version you must
regenerate the lock and verification files in the same commit, otherwise the build will fail:

```bash
# 1. Re-resolve and rewrite the lockfiles
./gradlew dependencies --write-locks

# 2. Refresh checksums, signatures and the exported keyring
./gradlew --write-verification-metadata pgp,sha256 --export-keys --refresh-dependencies build

# 3. We keep only the ascii-armored keyring; drop the binary copy if it was regenerated
rm -f gradle/verification-keyring.gpg
```

Review any new trusted keys before committing — that review is the whole point of signature
verification. Dependabot updates `gradle.lockfile` automatically, but it does **not** touch the
verification metadata, so its version-bump PRs need the steps above run on top.

If you are unsure about how something should be implemented, open a pull request and we'll discuss it.
