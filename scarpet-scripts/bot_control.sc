__config() -> {
    'scope' -> 'global'
};

global_bot_configs = {
    'bot_vbreeder' -> {
        'position' -> [-3010, 53, -1152],
        'facing' -> [0, 0],
        'dimension' -> 'minecraft:overworld',
        'mode' -> 'survival',
        'post_spawn' -> ['player bot_vbreeder sneak']
    },
    'bot_slime' -> {
        'position' -> [-858, 173, -12902],
        'facing' -> [0, 0],
        'dimension' -> 'minecraft:overworld',
        'mode' -> 'survival',
        'post_spawn' -> ['player bot_slime sneak']
    },
    'bot_sand' -> {
        'position' -> [-12559.5, 61, 1562.5],
        'facing' -> [90, 0],
        'dimension' -> 'minecraft:overworld',
        'mode' -> 'survival',
        'post_spawn' -> ['player bot_sand sneak']
    },
    'bot_lazer_fish' -> {
        'position' -> [-3216.02, 14.00, -1919.99],
        'facing' -> [0, 0],
        'dimension' -> 'minecraft:overworld',
        'mode' -> 'survival',
        'post_spawn' -> ['player bot_lazer_fish sneak']
    },
    'bot_magma' -> {
        'position' -> [-409.5, 254.5, -125.5],
        'facing' -> [0, 0],
        'dimension' -> 'minecraft:the_nether',
        'mode' -> 'survival',
        'post_spawn' -> ['player bot_magma sneak']
    },
    'bot_raid' -> {
        'position' -> [-4581.50, 73.00, -1110.5],
        'facing' -> [-45, 90],
        'dimension' -> 'minecraft:overworld',
        'mode' -> 'survival',
        'post_spawn' -> [
            'clear bot_raid',
            'give bot_raid diamond_sword{Enchantments:[{id:sharpness,lvl:5},{id:sweeping,lvl:3},{id:looting,lvl:3}]}',
            'item replace entity bot_raid armor.head with diamond_helmet{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'item replace entity bot_raid armor.chest with diamond_chestplate{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'item replace entity bot_raid armor.legs with diamond_leggings{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'item replace entity bot_raid armor.feet with diamond_boots{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'give bot_raid totem_of_undying 16',
            'player bot_raid attack interval 30',
            'effect give bot_raid minecraft:bad_omen'
        ],
        'pre_kill' -> ['clear bot_raid']
    },
    'bot_fortress' -> {
        'position' -> [-353.7, 24.00, -178.7],
        'facing' -> [-49.0, -21.6],
        'dimension' -> 'minecraft:the_nether',
        'mode' -> 'survival',
        'post_spawn' -> [
            'clear bot_fortress',
            'give bot_fortress diamond_sword{Enchantments:[{id:sharpness,lvl:5},{id:sweeping,lvl:3},{id:looting,lvl:3}]}',
            'item replace entity bot_fortress armor.head with diamond_helmet{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'item replace entity bot_fortress armor.chest with diamond_chestplate{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'item replace entity bot_fortress armor.legs with diamond_leggings{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'item replace entity bot_fortress armor.feet with diamond_boots{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}',
            'give bot_fortress totem_of_undying 16',
            'player bot_fortress attack interval 20'
        ],
        'pre_kill' -> ['clear bot_fortress']
    }
};

__on_start() -> (
    global_save_bot_state_is_scheduled = false;
);

__on_server_starts() -> (
    data = read_file('bot_control', 'json');
    if (data, for (data, _spawn_bot(_)));
);

__on_server_shuts_down() -> (
    __save_bot_state();
);

__save_bot_state() -> (
    global_save_bot_state_is_scheduled = false;
    delete_file('bot_control', 'json');
    data = [];
    for (filter(player('all'), _~'player_type' == 'fake'), data += _~'name');
    write_file('bot_control', 'json', data);
);

__schedule_save() -> (
    if (!global_save_bot_state_is_scheduled, (
        schedule(2000, '__save_bot_state');
        global_save_bot_state_is_scheduled = true;
    ));
    return;
);

_spawn_bot(bot_name) -> (
    config = global_bot_configs:bot_name;
    if (config,
        pos = config:'position';
        facing = config:'facing';
        dim = config:'dimension';
        mode = config:'mode';
        run(str('player %s spawn at %s %s %s facing %s %s in %s in %s', bot_name, pos:0, pos:1, pos:2, facing:0, facing:1, dim, mode));
        if (config:'post_spawn',
            for (config:'post_spawn', run(_))
        );
        __schedule_save();
    ,
        print('Unknown bot: ' + bot_name)
    )
);

_kill_bot(bot_name) -> (
    config = global_bot_configs:bot_name;
    if (config,
        if (config:'pre_kill',
            for (config:'pre_kill', run(_))
        );
        run(str('player %s kill', bot_name));
        __schedule_save();
    ,
        print('Unknown bot: ' + bot_name)
    )
);

_toggle_bot(bot_name) -> (
    p = player(bot_name);
    if (p,
        _kill_bot(bot_name)
    ,
        _spawn_bot(bot_name)
    )
);

__command() -> {
    map = {};
    for (keys(global_bot_configs),
        bot = _;
        map:bot = {
            'spawn' -> _() -> _spawn_bot(bot),
            'kill' -> _() -> _kill_bot(bot),
            'toggle' -> _() -> _toggle_bot(bot)
        }
    );
    map
};

bot_villager_breeder() -> _toggle_bot('bot_vbreeder');
bot_slime_farm() -> _toggle_bot('bot_slime');
bot_sand_farm() -> _toggle_bot('bot_sand');
bot_guardian_farm() -> _toggle_bot('bot_lazer_fish');
bot_magma_cube_farm() -> _toggle_bot('bot_magma');
bot_raid_farm() -> _toggle_bot('bot_raid');
bot_fortress_farm() -> _toggle_bot('bot_fortress');
