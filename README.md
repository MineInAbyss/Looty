<div align="center">

# Looty

[![Java CI with Gradle](https://github.com/MineInAbyss/Looty/actions/workflows/gradle-ci.yml/badge.svg)](https://github.com/MineInAbyss/Looty/actions/workflows/gradle-ci.yml)
[![Maven](https://img.shields.io/maven-metadata/v?metadataUrl=https://repo.mineinabyss.com/releases/com/mineinabyss/looty/maven-metadata.xml)](https://repo.mineinabyss.com/#/releases/com/mineinabyss/looty)
[![Contribute](https://shields.io/badge/Contribute-e57be5?logo=github%20sponsors&style=flat&logoColor=white)](https://wiki.mineinabyss.com/contribute)
</div>

Looty is a [Paper](https://papermc.io/) plugin for creating custom items with config files. We use [Geary](https://github.com/MineInAbyss/geary-papermc) to break down items into small components. We provide many components to modify vanilla behaviour, for new game features check out [Geary-addons](https://github.com/MineInAbyss/Geary-addons).

## Features

- Automatically updates item name, lore, and custom models when the config changes
- Support for custom recipes
- Write configs in yaml, json, and more.

## Usage
Coming soon

## Examples

### Custom item from our server

`star-compass.yml`
```yaml
# Make items in the same inventory share one entity
- !<looty:player_instanced_item>

# Specify item name, model, and lore,
# if this ever changes, Looty will update existing items
- !<looty:type>
  item:
    type: PAPER
    customModelData: 125
    displayName: <dark_aqua>Star Compass
    lore:
      - <aqua>Points towards the center of the <green>Abyss

# Add a recipe using other custom items
- !<looty:recipes>
  discoverRecipes: true
  recipes:
    - !<shapeless>
      items:
        - prefab: mineinabyss:star_compass_needle
        - prefab: mineinabyss:titanjaw_pearl


# A component provided another plugin
- !<mineinabyss:starcompass>

```

### Custom recipe for a vanilla item

`lead.yml`
```yaml
- !<looty:recipes>
  discoverRecipes: true
  result:
    type: LEAD
    amount: 2
  removeRecipes:
    - minecraft:lead
  group: "looty:lead"
  recipes:
    - !<shaped>
      items:
        S:
          prefab: mineinabyss:silkfang_silk
        Z:
          prefab: mineinabyss:abyssal_snail_gunk
      configuration: |-
        |ZZ |
        |ZS |
        |  Z|
```
