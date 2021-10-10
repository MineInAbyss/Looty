<div align="center">

# Looty
[![Java CI with Gradle](https://github.com/MineInAbyss/Looty/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/MineInAbyss/Looty/actions/workflows/gradle-ci.yml)
[![Package](https://badgen.net/maven/v/metadata-url/repo.mineinabyss.com/releases/com/mineinabyss/looty/maven-metadata.xml)](https://repo.mineinabyss.com/releases/com/mineinabyss/looty)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://wiki.mineinabyss.com/contribute)
</div>
  
## Overview

Looty is a PaperMC plugin that acts as a link between ItemStacks and ECS entities from our Entity Component System (ECS) [Geary](https://github.com/MineInAbyss/Geary). It lets you easily persist components on ItemStacks and have systems iterate over them. 

Looty also provides the same powerful configuration system as Geary, which lets you quickly create fancy custom items for Minecraft servers. It even updates item data like models or lore update automatically.

## Features

### Modular behaviours

ECS allows us to deconstruct complex item behaviours into individual components or actions. It makes code easier to maintain and behaviours more reusable!

### Easy serialization

Thanks to kotlinx.serialization all our components are automatically serializable without reflection! Looty will automatically save persisting components to the item itself.

### Prefabs and config files

Looty uses the same prefab system as Geary. It also adds item-related events so you can configure Geary actions to fire on left click, or when an item is equipped.

Coders can focus on coding interesting components and systems while designers can tweak numbers and combine things together without messing with your precious code!

### Item tracking

Looty will automatically keep track of item entities for you. You can go between the two using: `gearyOrNull(itemStack)` and `gearyEntity.get<ItemStack>()`. Looty essentially ensures the ItemStack component on this entity always references the true ItemStack (i.e. modifying it modifies the item in inventory.)

Currently, we plan on keeping track of items in player inventories and when thrown on the ground. We may also allow specifically marked mobs to keep track of custom items in their inventory (doing this for all mobs would likely be unnecessarily slow).

## Biggest issues
- Some more caching and optimizations should be done for the item tracking system. We haven't tested it large-scale yet.
- There are many design questions regarding how the config system should work and how to handle some complex behaviours. Things may change.
- There is no data migration for items, though using prefabs, most item components can be static and not serialized to the item itself. This means once the prefab config is updated, the item itself works again.
