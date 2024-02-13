__config() -> {
    'scope' -> 'global'
};

__on_server_starts() -> (
    data = read_file('bot_control', 'json');
    for (data,call(_));
);

__on_server_shuts_down() -> (
    delete_file('bot_control', 'json');
    data = [];

    for (filter(player('all'), _~'player_type' == 'fake'),
        if (_~'name' == 'bot_vbreeder', data += 'spawn_bot_villager_breeder');
        if (_~'name' == 'bot_slime', data += 'spawn_bot_slime_farm');
        if (_~'name' == 'bot_lazer_fish', data += 'spawn_bot_guardian_farm');
        if (_~'name' == 'bot_magma', data += 'spawn_bot_magma_cube_farm');
        if (_~'name' == 'bot_raid', data += 'spawn_bot_raid_farm');
    );
    write_file('bot_control', 'json', data);
);

__command() -> 'root command';

spawn_bot_villager_breeder() -> (
    run('player bot_vbreeder spawn at -3010 53 -1152 facing 0 0 in minecraft:overworld in survival');
    run('player bot_vbreeder sneak');
);

kill_bot_villager_breeder() -> (
    run('player bot_vbreeder kill');
);

spawn_bot_slime_farm() -> (
    run('player bot_slime spawn at -858 173 -12902 facing 0 0 in minecraft:overworld in survival');
    run('player bot_slime sneak');
);

kill_bot_slime_farm() -> (
    run('player bot_slime kill');
);

spawn_bot_guardian_farm() -> (
    run('player bot_lazer_fish spawn at -3216.02 14.00 -1919.99 facing 0 0 in minecraft:overworld in survival');
    run('player bot_lazer_fish sneak');
);

kill_bot_guardian_farm() -> (
    run('player bot_lazer_fish kill');
);

spawn_bot_magma_cube_farm() -> (
    run('player bot_magma spawn at -409.5 254.5 -125.5 facing 0 0 in minecraft:the_nether in survival');
    run('player bot_magma sneak');
);

kill_bot_magma_cube_farm() -> (
    run('player bot_magma kill');
);

spawn_bot_raid_farm() -> (
    run('player bot_raid spawn at -4581.50 73.00 -1110.5 facing -45 90 in minecraft:overworld in survival');
    run('clear bot_raid');
    run('give bot_raid diamond_sword{Enchantments:[{id:sharpness,lvl:5},{id:sweeping,lvl:3}]}');
    run('item replace entity bot_raid armor.head with diamond_helmet{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}');
    run('item replace entity bot_raid armor.chest with diamond_chestplate{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}');
    run('item replace entity bot_raid armor.legs with diamond_leggings{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}');
    run('item replace entity bot_raid armor.feet with diamond_boots{Enchantments:[{lvl:4,id:blast_protection},{lvl:4,id:fire_protection},{lvl:4,id:projectile_protection},{lvl:4,id:protection}]}');
    run('give bot_raid totem_of_undying 16');
    run('player bot_raid attack interval 30');
    run('effect give bot_raid minecraft:bad_omen')
);

kill_bot_raid_farm() -> (
    run('clear bot_raid');
    run('player bot_raid kill');
);
