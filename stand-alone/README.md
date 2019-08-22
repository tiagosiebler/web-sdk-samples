# Stand-alone MicroStrategy Java Web SDK Samples

These are samples demonstrating various ways to use different parts of the MicroStrategy Web SDK in a standalone java application. These do not advise best practices and purely demonstrate one way these can be used, feel free to adapt and recycle parts of this as needed.

Contributions via Pull Requests are very welcome.

## Setup
This `stand-alone` directory can be used directly within your IDE. The following IDE-specific guides are available:
- [Eclipse](./setup_eclipse.md)

## Dependencies
MicroStrategy dependencies are the jar libraries found in any MicroStrategy Web installation, in the following directory:
> webapps/MicroStrategy/WEB-INF/lib/

These samples were created using Eclipse, though any Java IDE should be usable as long as the class path is correctly configured, and the correct java versions are used.

## Versioning
While many APIs exist in previous releases, due to the nature of Web API <-> Intelligence Server communciation, java classes should be compiled using libraries from the same Web version as the Intelligence Server.

A mismatch between the Java Web API version and the Intelligence Server version may introduce unforeseen complications.

## Further Reading

1. [developer.microstrategy.com](https://developer.microstrategy.com) - The SDK landing page for official SDK documentation and references.
2. [MicroStrategy Java API Reference](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/ReferenceFiles/index.html) - class and method documentation. Search using the name of the class or interface.
3. [Web SDK Documentation](https://lw.microstrategy.com/msdz/MSDL/GARelease_Current/docs/projects/WebSDK/Content/topics/other/Introduction_to_the_Web_SDK.htm) - the core landing page for all MicroStrategy Web SDK documentation, including underlying architectural explanations.