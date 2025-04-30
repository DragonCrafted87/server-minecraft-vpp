__config() -> {
    'scope' -> 'player'
};

__on_start() -> (
    global_flatten_bedrock_interval = 20;
    __flatten_bedrock();

);

global_position_offset = 32;

__flatten_bedrock() -> (
    player_position = player() ~ 'pos';
    player_position_x = player_position:0;
    player_position_z = player_position:2;

    if (player() ~ 'dimension' == 'overworld',
    (
        volume([player_position_x-global_position_offset, -63, player_position_z-global_position_offset],
               [player_position_x+global_position_offset, -60, player_position_z+global_position_offset],
        (
            current_block = block(_);
            if (_ == 'bedrock',
            (
                current_block_position = pos(_);
                solid_block_clount = for(neighbours(current_block_position),_ == 'deepslate');
                liquid_block_clount = for(neighbours(current_block_position),_ == 'lava');

                if (liquid_block_clount > solid_block_clount,
                (
                    set(current_block_position,'lava');
                ),
                // else
                (
                    set(current_block_position,'deepslate');
                )
                );
            ));
        ));

        volume([player_position_x-global_position_offset, -64, player_position_z-global_position_offset],
               [player_position_x+global_position_offset, -64, player_position_z+global_position_offset],
        (
            if (_ != 'bedrock' && _ != 'air',
            (
                set(pos(_),'bedrock');
            ));
        ));
    ),
    player() ~ 'dimension' == 'the_nether',
    (
        volume([player_position_x-global_position_offset, 1, player_position_z-global_position_offset],
               [player_position_x+global_position_offset, 4, player_position_z+global_position_offset],
        (
            current_block = block(_);
            if (_ == 'bedrock',
            (
                current_block_position = pos(_);
                solid_block_clount = for(neighbours(current_block_position),_ == 'netherrack');
                liquid_block_clount = for(neighbours(current_block_position),_ == 'lava');

                if (liquid_block_clount > solid_block_clount,
                (
                    set(current_block_position,'lava');
                ),
                // else
                (
                    set(current_block_position,'netherrack');
                )
                );
            ));
        ));

        volume([player_position_x-global_position_offset, 126, player_position_z-global_position_offset],
               [player_position_x+global_position_offset, 123, player_position_z+global_position_offset],
        (
            current_block = block(_);
            if (_ == 'bedrock',
            (
                current_block_position = pos(_);
                solid_block_clount = for(neighbours(current_block_position),_ == 'netherrack');
                liquid_block_clount = for(neighbours(current_block_position),_ == 'lava');

                if (liquid_block_clount > solid_block_clount,
                (
                    set(current_block_position,'lava');
                ),
                // else
                (
                    set(current_block_position,'netherrack');
                )
                );
            ));
        ));

        volume([player_position_x-global_position_offset, 0, player_position_z-global_position_offset],
               [player_position_x+global_position_offset, 0, player_position_z+global_position_offset],
        (
            if (_ != 'bedrock' && _ != 'air',
            (
                set(pos(_),'bedrock');
            ));
        ));
    ),
    (

    )
    );

    schedule(global_flatten_bedrock_interval, '__flatten_bedrock');
    return;
);
