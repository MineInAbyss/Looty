# Looty
[![Java CI with Gradle](https://github.com/MineInAbyss/Looty/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/MineInAbyss/Looty/actions/workflows/gradle-ci.yml)
[![Package](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/looty/maven-metadata.xml)](https://repo.mineinabyss.com/releases/com/mineinabyss/looty)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://wiki.mineinabyss.com/contribute)

## Overview

Looty is a spigot plugin for creating custom items using our own Geary Entity Component System (ECS). It tries to bring together the super modular design of an ECS with a powerful configuration system to quickly create fancy custom items for Minecraft servers.

## Features/Goals 
*(most are WIP :p)*

### Modular behaviours

ECS allows us to deconstruct complex item behaviours into individual components which we can then add to our items. It makes code easier to maintain and behaviours more reusable!

### Easy serialization

Thanks to kotlinx.serialization all our components are automatically serializable without reflection! We can then read them from item config files or save them directly to an item's persistent data container.

### Config based items

We provide a config system that allows for creating more complex item behaviours, for example adding components when a spigot event relating to the item occurs, or if a certain condition is met.

Coders can focus on coding interesting components and systems while designers can tweak numbers and combine things together without messing with your precious code!

### Item tracking

Looty will automatically keep track of item entities for you. Essentially, its job is to link Minecraft's concept of an ItemStack (an instance of which will quickly become outdated) to actual ECS entities.

Currently, we plan on keeping track of items in player inventories and when thrown on the ground. We may also allow specifically marked mobs to keep track of custom items in their inventory (doing this for all mobs would likely be too slow).

## Biggest issues
- Lots of caching needs to be done to efficiently keep track of items.
- There are many design questions regarding how the config system should work and how to handle some complex behaviours.
- There is no data migration for items, though if using the config system, most important item components would be static and not serialized to the item itself, meaning a config update would often fix issues for all items of that type.
