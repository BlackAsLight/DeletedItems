# Deleted Items
This plugin was made with the IntelliJ IDEA. I don't know how to build it with anything else.

If you get any errors in the console from this plugin, please open an issue here reporting it as well how you created it and a copy of the items.yml file.

## What Is This?

This plugin detects when items despawn or get deleted (through methods of lava, tnt, fire, cactus, tnt minecart, wither explosion, lightning strike, and any other way you can think of items taking damage) excluding items that fall into the void.

When it detects that these items get deleted it catches the item and places it into a GUI shop, that is accessed through `/di` command, where players can buy it from the server. If the item had a spacial name put on it by a player, that name will be removed before entering the Shop.

Damaged Items, like swords, bows, pickaxes, etc, in the Shop lose one durability every half an hour (enchantments like unbreaking don't slow this process.) and once they run out of durability they get deleted from the shop. When these items enter the Shop undamaged they will be exlcuded from losing durability. This removes unwanted tools that acumulate from farms and just general gameplay that no player is likely to buy. This also reduces players ability to delude the shop's prices since the same item with different durability show up seperately in the shop and therefore count as two seperate unque items. For example if a tool has 500 durability left on it then it will take roughly 10.4 days for it to lose that durability and get removed from the Shop.

### GUI Description:
 - The shop GUI is the size of a double chest, with the first 5 rows being slots to buy stuff and the last bottom row being buttons for navigation and stuff.
 - Each item for sale in the shop is only displayed once.
 - Hovering over the item with your mouse will tell you the price of the item and how much is available.
 - The amount you can buy per click is the stack size displayed on the player's screen. There is candle buttons in the bottom row which allow the player to increase/decrease the current stack size by 1, 4 and 8.
 - When you open the shop GUI, with the `/di` command, you start on page 0. There is a paper bottom at each side of the bottom row to move to the next or previous page.
 - There is a book button at the centre of the bottom row which allows you to toggle those next/previous page buttons to last/first page buttons.
 - The items for sale in the shop is ordered in decending order of stock available, however it only checks the order periodically.
 - Items that can take damage, like sowrds, tools, and armour, lose one durability every half an hour until they're either purchased by a player or run out and deleted from the shop. Elytra is not exempt from hitting zero durability and getting deleted even though the lowest you can work it down to from flying is 1 durability.

### Price Calculation:

The Price of an item in the shop changes dynmaically based off what is in the shop, how much stuff, and how much different stuff is in the shop, as well as if the items in the shop have enchantments or are damaged in any way. The forumula that calculates the price of an item in the shop is simple:

`Price = (Number of Items in Shop - Amount of this Item in the Shop) / Number of unquie items in the Shop / Max Stack size of this Item * Enchantment Modifier * Item Durability`

#### Examples
```JavaScript
ShopsContents: [
  '5 Cobblestone',
  '8 Dirt',
  '16 Eggs',
  '1 Diamond Sword',
  {
    Item: '1 Iron Pickaxe',
    Enchantments: [
      'Mending',
      'Unbreaking III',
      'Fortune I'
    ]
  },
  {
    Item: '1 Stone Shovel',
    Enchantments: [
      'Unbreaking I'
    ],
    Durability: '50/100'
  },
  {
    Item: '1 Gold Sword',
    Durability: '3/150'
  }
]
```

**Example I:**
If we wanted to calculate for say Dirt, we'd need to:
1. Calculate how many items are in the shop: `5 Cobblestone + 8 Dirt + 16 Eggs + 1 Diamond Sword + 1 Iron Pickaxe + 1 Stone Shovel + 1 Gold Sword = 33 Items`
2. Then we'd need to see how much Dirt is in the shop: `8 Dirt`.
4. Now we need to calculate how many different items are in the shop: `Cobblestone, Dirt, Eggs, Diamond Sword, Iron Pickaxe, Stone Shovel, and Gold Sword = 7 Different Items`
5. The Max stack size of Dirt is 64.
6. Since Dirt cannot be enchanted it has an Enchantment Modifer of 1.
7. Since Dirt cannot take damage it has a Durability of 100%.
8. With all these numbers we simply fill out the formula above to calculate what the price of Dirt would be based off the current shop's contents:
```
Price = (33 - 8) / 7 / 64 * 1 * 100%
Price = 25 / 7 / 64 * 1 * 100%
Price = ~3.57 / 64 * 1 * 100%
Price = ~0.06 / 1 * 100%
Price = ~0.06 * 100%
Price = ~0.06
```
9. The end result will be rounded to two decimal places so Dirt will be $0.06/each.

**Example II:**
To calculate for say the Enchanted Iron Pickaxe, we'd do it the same way we did it for Dirt in Example I, with there being 33 Items in the shop, 7 different Items in the shop, 1 Iron Pickaxe, a max stack size of 1 for Iron Pickaxes, and since this pickaxe is not damaged it will have a durability of 100%.

*Note: Since it has a durability of 100% in this example it will not lose durability over time.*

To calculate the Enchantment Modifer we simply look at the level of every enchantment on the item and divide that enchantment's level by that enchantment's max level then mulipy it by 5.
 - For Mending: `1 / 1 * 5 = 5`
   - Since the level of Mending on the item is 1, and the max level you can have is 1, the modifer is increased by 5.
 - For Unbreaking: `3 / 3 * 5 = 5`
   - Since the level of Unbreaking on the item is 3, and the max level you can have is 3, the modifer is increased by 5.
 - For Fortune: `1 / 3 * 5 = ~1.67`
   - Since the level of Fortune on the item is 1, and the max level you can have is 3, the modifer is increased by ~1.67.
These amounts (5, 5, ~1.67) are summed up together and make the Enchantment Modifer: `5 + 5 + ~1.67 = ~11.67`
```
Price = (33 - 1) / 7 / 1 * ~11.67 * 100%
Price = ~4.57 * ~11.67
Price = ~53.33
```

**Example III:**
Lets now calculate something damaged like the Gold Sword. When we do it like example one, we'd get 33 Items, 7 different Items in the shop, 1 Gold Sword, a max size of 1 for Gold Swords, and since this sowrd has no enchantments it has a Enchantment Modifer of 1.

To calculate it's durability we simply take how much durability it has left and divide that by it's max durability: `3 / 150 = 0.02 = 2%`

*Note: In the example above I've stated that the max durability is 150, I don't know if this is true in game or not as I have not checked, but for example purposes we will say that the max durability of a golden Sword is 150.*

```
Price = (33 - 1) / 7 / 1 * 1 * 2%
Price = ~4.57 * 2%
Price = ~0.09
```

**Example IV:**
Lastly if we try and calculate the price for the Stone Shovel, based off the knowledge we've gained above it would be:
```
Price = (33 - 1) / 7 / 1 * ~1.67 * 50%
Price = ~4.57 * ~1.67 * 50%
Price = ~7.62 * 50%
Price = ~3.81
```
*Note: The price isn't rounded until the end meaning if you're doing calculations by hand that you shouldn't round and try to maintain 15 decmial points otherwise you could get rounding errors.*

## Requirements

### Java 16

This plugin was built with Java 16. It might be able to be compiled with earlier/later versions of Java, but I didn't test it.

### Spigot 1.17.1

This plugin was made and tested on version 1.17.1 of the Spigot API. Should work on later versions of minecraft as long as the APIs it uses don't break.

### Vault Plugin

This plugin requires the [Vault](https://www.spigotmc.org/resources/vault.34315/) plugin **and** an economy plugin that also implements [Vault](https://www.spigotmc.org/resources/vault.34315/).
