// priority: 0


StartupEvents.registry('menu', event => {
    event.create('example_block' /*name has to be same as BlockEntity's id*/, 'block_entity')
        .addSlot(-10, -10) // adds a slot into this x,y position on the texture
        .addSlot(10, 200)
        .loop(builder /*this builder*/=> {
            for(let x = 0; x < 9; x++) {
                for (let y = 0; y < 4; y++) {
                    builder.addSlot(x * 18 /*<- the width of a slot, remember to add this*/, y * 18)
                }
            }
        })
        .addOutputSlot(118, 118) // adds a slot you can't put an item into, but can pull an item from
        .playerInventoryY(100) // marks the start of the player's inventory on the texture
        .tintColor(0x00FF00FF) // a color to tint the whole inventory texture, in hexadecimal [r, g, b, a]

    event.create('grass_block' /*name can be anything*/, 'block')
        .addSlot(100, 82)
        .addSlot(100, 100)
        .addOutputSlot(118, 118)
        .playerInventoryY(100)
        .tintColor(0xFF00FF00)
        .setBlock('minecraft:grass_block') // the block that should open this GUI on right-click
})
