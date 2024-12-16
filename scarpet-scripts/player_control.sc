__config() -> {
    'scope' -> 'player'
};

__on_start() -> (
    global_ender_render_active = false;
);


__command() -> 'root command';


global_ender_render_attack_total = 5;
global_ender_render_attack_interval = 12;
global_ender_render_wait_interval = 20 * 8;

ender_render_start() -> (
    global_ender_render_active = true;
    global_ender_render_attack_count = 0;
    __ender_render_attack(player());
    return;
);

ender_render_stop() -> (
    global_ender_render_active = false;
    return;
);

__ender_render_attack(player) -> (
    if(global_ender_render_active,
    (
        run(str('player %s attack once', player));
        global_ender_render_attack_count = global_ender_render_attack_count + 1;
        if (global_ender_render_attack_count < global_ender_render_attack_total,
        (
            schedule(global_ender_render_attack_interval, '__ender_render_attack', player);
        ),
        (
            global_ender_render_attack_count = 0;
            schedule(global_ender_render_wait_interval, '__ender_render_attack', player);
        ));
    ));
);
