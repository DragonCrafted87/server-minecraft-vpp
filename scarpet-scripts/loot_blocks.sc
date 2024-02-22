__config() -> {
    'scope' -> 'global'
};

silk_drop(block_name) -> (
    block_name = str('minecraft:%s', block_name);

    file_contents =  {
        'type'-> 'minecraft:block',
        'pools'-> [
            {
                'bonus_rolls'-> 0.0,
                'conditions'-> [
                    {
                        'condition'-> 'minecraft:match_tool',
                        'predicate'-> {
                        'enchantments'-> [
                            {
                                'enchantment'-> 'minecraft:silk_touch',
                                'levels'-> {
                                    'min'-> 1
                                }
                            }
                        ]
                        }
                    }
                    ],
                'entries'-> [
                    {
                        'type'-> 'minecraft:item',
                        'name'-> block_name
                    }
                ],
                'rolls' -> 1.0
            }
        ]
    };

    return(file_contents);
);

__on_start() -> (
    success = create_datapack('dragoncrafted87_loot_blocks',
        {
            'readme.txt' -> ['this data pack is created by scarpet','please dont touch it'],
            'data' -> {
                'minecraft' ->{
                    'loot_tables'->{
                        'blocks'->{
                            'budding_amethyst.json' -> silk_drop('budding_amethyst'),
                            'reinforced_deepslate.json' -> silk_drop('reinforced_deepslate'),
                            'spawner.json' -> silk_drop('spawner'),
                        }
                    }
                }
            }
        }
    );
);
