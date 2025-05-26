__config() -> {
    'scope' -> 'player',
    'commands' -> {
        'ender_render_toggle' -> 'ender_render_toggle',
        'debug <onoff>' -> 'toggle_debug'
    }
};

__on_start() -> (
    global_ender_render_active = false;
    global_debug = false; // Initialize debug mode to off
);

toggle_debug(onoff) -> (
    global_debug = onoff == 'on';
    if(global_debug,
        print(player(), format('gi Script: Player Control: Debug mode enabled')),
        print(player(), format('gi Script: Player Control: Debug mode disabled'))
    );
);

global_ender_render_attack_total = 5;
global_ender_render_attack_interval = 12;
global_ender_render_wait_interval = 20 * 8;

// List of durable item suffixes (tools, armor, weapons, etc.)
global_durable_suffixes = l(
    'pickaxe', 'axe', 'shovel', 'hoe', 'sword', 'helmet', 'chestplate', 'leggings', 'boots',
    'shield', 'bow', 'crossbow', 'trident', 'fishing_rod', 'shears', 'flint_and_steel'
);

__is_item_durable(item_id, player) -> (
    is_durable = false;
    for(global_durable_suffixes,
        if(item_id ~ _,
            is_durable = true;
            break();
        );
    );
    if(global_debug && player != null,
        print(player, format('gi Script: Player Control: Is item ' + str(item_id) + ' durable: ' + str(is_durable)))
    );
    is_durable
);

ender_render_toggle() -> (
    global_ender_render_active = !global_ender_render_active;
    if(global_ender_render_active,
        global_ender_render_attack_count = 0;
        __ender_render_attack(player());
        print(player(), format('gi Script: Player Control: Ender render attack started'));
    ,
        print(player(), format('gi Script: Player Control: Ender render attack stopped'));
    );
);

__ender_render_attack(player) -> (
    if(global_ender_render_active,
        run(str('player %s attack once', player));
        global_ender_render_attack_count = global_ender_render_attack_count + 1;
        if(global_ender_render_attack_count < global_ender_render_attack_total,
            schedule(global_ender_render_attack_interval, '__ender_render_attack', player);
        ,
            global_ender_render_attack_count = 0;
            __check_and_swap_offhand(player);
            schedule(global_ender_render_wait_interval, '__ender_render_attack', player);
        );
    );
);

__get_enchantment_level(player, tool_nbt, enchantment) -> (
    level = 0;
    if ((enchantments = get(tool_nbt,'Enchantments[]')),
        global_debug && print(player, format('gi Script: Player Control: Tool Enchantments: ' + str(enchantments)));
        if (type(enchantments)!='list', enchantments = l(enchantments));
        for (enchantments,
            if ( get(_,'id') == 'minecraft:' + enchantment,
                level = max(level, get(_,'lvl'))
            )
        )
    );
    level
);

__is_offhand_eligible(player, offhand) -> (
    if(!offhand, return(false)); // No item in offhand
    [item_id, count, nbt] = offhand;
    // Check if the item is durable (tools, armor, etc.)
    if(!__is_item_durable(item_id, player), return(false));
    // Check if offhand item has Mending enchantment
    has_mending = false;
    __get_enchantment_level(player, nbt, 'mending') > 0 && (has_mending = true); // Check for Mending enchantment
    if(global_debug, print(player, format('gi Script: Player Control: Offhand Has Mending: ' + str(has_mending))));
    if(!has_mending, return(false)); // No Mending enchantment
    // Check if offhand item is fully repaired (Damage:0 or no Damage tag)
    damage = get(nbt, 'Damage');
    if(global_debug, print(player, format('gi Script: Player Control: Offhand damage: ' + str(damage))));
    if(damage != null && damage != 0, return(false)); // Not fully repaired
    true // Offhand is eligible
);

__find_damaged_mending_item(player) -> (
    damaged_slot = null;
    damaged_item = null;
    if(global_debug,
        inventory_contents = l();
        for(range(9, 36),
            slot_item = inventory_get(player, _);
            if(slot_item, inventory_contents += str('Slot ' + _ + ': ' + str(slot_item)));
        );
        if(length(inventory_contents) > 0,
            print(player, format('gi Script: Player Control: Main inventory contents (slots 9–35): ' + str(inventory_contents))),
            print(player, format('gi Script: Player Control: Main inventory contents (slots 9–35): Empty'))
        );
    );
    for(range(9, 36), // Main inventory slots
        slot_item = inventory_get(player, _);
        if(slot_item,
            [slot_item_id, slot_count, slot_nbt] = slot_item;
            slot_damage = get(slot_nbt, 'Damage');
            if(slot_damage != null && slot_damage > 0, // Item is damaged and repairable
                // Check if the item is durable
                if(__is_item_durable(slot_item_id, player),
                    slot_has_mending = false;
                    __get_enchantment_level(player, slot_nbt, 'mending') > 0 && (slot_has_mending = true); // Check for Mending enchantment
                    if(slot_has_mending,
                        damaged_slot = _;
                        damaged_item = slot_item;
                        break();
                    );
                );
            );
        );
    );
    if(global_debug, print(player, format('gi Script: Player Control: Damaged Mending item found at slot: ' + str(damaged_slot) + ', item: ' + str(damaged_item))));
    [damaged_slot, damaged_item]
);

__swap_items(player, damaged_slot, offhand_item, damaged_item) -> (
    if(global_debug, print(player, format('gi Script: Player Control: Swapping offhand with slot ' + str(damaged_slot))));
    // Set offhand to inventory item
    [inventory_item_id, inventory_count, inventory_nbt] = damaged_item;
    inventory_set(player, -1, inventory_count, inventory_item_id, inventory_nbt);
    if(global_debug, print(player, format('gi Script: Player Control: Set offhand to ' + str(damaged_item))));

    // Set inventory slot to offhand item
    [offhand_item_id, offhand_count, offhand_nbt] = offhand_item;
    inventory_set(player, damaged_slot, offhand_count, offhand_item_id, offhand_nbt);
    if(global_debug, print(player, format('gi Script: Player Control: Set slot ' + str(damaged_slot) + ' to ' + str(offhand_item))));
    print(player, format('gi Script: Player Control: Swapped fully mended offhand item with damaged Mending item from inventory'));
    true
);

__check_and_swap_offhand(player) -> (
    if(global_debug, print(player, format('gi Script: Player Control: Player entity: ' + str(player))));
    offhand = query(player, 'holds', 'offhand');
    if(global_debug, print(player, format('gi Script: Player Control: Offhand item: ' + str(offhand))));
    if(!__is_offhand_eligible(player, offhand), return());
    [damaged_slot, damaged_item] = __find_damaged_mending_item(player);
    if(damaged_slot == null, return()); // No qualifying item found
    __swap_items(player, damaged_slot, offhand, damaged_item);
);
