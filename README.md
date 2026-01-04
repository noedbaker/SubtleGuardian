# SubtleGuardian - Noah Baker & Fabric
A server-side plugin for Minecraft Fabric Servers. Allow yourself to overcome death through a saving grace.


In order to add this plugin to your Minecraft Server, you must be running the following specializations:

  -Minecraft Java Version 1.21.11
  -Fabric Loader 0.18.4
  -Fabric API Version 0.140.2+ 1.21.11 [release]

Adjustable settings in /src/main/java/dev/noedbaker/subtleguardian folder, you may safely adjust the following in Subtleguardian.java:

  -DEBUG_CHAT on line 88
  -SAVE_COOLDOWN_MS on line 91
  -MICRO_HEAL_CHANCE on line 97

To build/save your newly edited JAR file, enter the following command in the VSC terminal in the directory of Subtleguardian.java:

  "./gradlew build"

Tada! Your .JAR file in the outer-most directory is ready for your server!

Thank you to the fabric developers for creating the Fabric loader and https://fabricmc.net/develop/template/ to help me create this project.
