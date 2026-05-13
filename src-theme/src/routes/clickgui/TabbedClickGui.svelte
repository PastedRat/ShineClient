<script lang="ts">
    import ClickGui from "./ClickGui.svelte";
    import GlobalSettings from "./tabs/GlobalSettings.svelte";
    import Tabs from "./tabs/Tabs.svelte";
    import {gridSize, os, scaleFactor, showGrid, snappingEnabled} from "./clickgui_store";
    import type {ConfigurableSetting, TogglableSetting} from "../../integration/types";
    import {onMount} from "svelte";
    import {getClientInfo, getGameWindow, getModuleSettings, setTyping} from "../../integration/rest";
    import {listen} from "../../integration/ws";
    import type {ClickGuiValueChangeEvent, ScaleFactorChangeEvent} from "../../integration/events";

    const tabs = [
        {title: "ClickGUI", content: ClickGui},
        {title: "Settings", content: GlobalSettings}
    ];

    let activeTab = $state(0);
    let minecraftScaleFactor = $state(2);
    let clickGuiScaleFactor = $state(1);

    type Star = { left: number; size: number; duration: number; delay: number; opacity: number; };

    const stars: Star[] = Array.from({length: 70}, () => ({
        left: Math.random() * 100,
        size: Math.random() * 2.8 + 1,
        duration: Math.random() * 10 + 10,
        delay: Math.random() * -20,
        opacity: Math.random() * 0.65 + 0.2
    }));


    $effect(() => {
        $scaleFactor = minecraftScaleFactor * clickGuiScaleFactor;
    });

    function applyValues(configurable: ConfigurableSetting) {
        const scaleValue = configurable.value.find(v => v.name === "Scale");
        const snappingValue = configurable.value.find(v => v.name === "Snapping") as TogglableSetting | undefined;

        if (scaleValue) {
            clickGuiScaleFactor = scaleValue.value as number;
        }

        if (snappingValue) {
            $snappingEnabled = snappingValue.value.find(v => v.name === "Enabled")?.value as boolean ?? true;
            $gridSize = snappingValue.value.find(v => v.name === "GridSize")?.value as number ?? 10;
        }
    }

    onMount(async () => {
        $os = (await getClientInfo()).os;

        const gameWindow = await getGameWindow();
        minecraftScaleFactor = gameWindow.scaleFactor;

        const clickGuiSettings = await getModuleSettings("ClickGUI");
        applyValues(clickGuiSettings);

        await setTyping(false);
    });

    listen("scaleFactorChange", (e: ScaleFactorChangeEvent) => {
        minecraftScaleFactor = e.scaleFactor;
    });

    listen("clickGuiValueChange", (e: ClickGuiValueChangeEvent) => {
        applyValues(e.configurable);
    });
</script>

<div class="starfield" aria-hidden="true">
    {#each stars as star}
        <span class="star" style="left: {star.left}%; width: {star.size}px; height: {star.size}px; opacity: {star.opacity}; animation-duration: {star.duration}s; animation-delay: {star.delay}s;"></span>
    {/each}
</div>

<div
        class="tabbed-clickgui"
        class:grid={$showGrid}
        style="
    transform: scale({$scaleFactor * 50}%);
    width: {2 / $scaleFactor * 100}vw;
    height: {2 / $scaleFactor * 100}vh;
    background-size: {$gridSize}px {$gridSize}px;
  "
>
    <Tabs {tabs} bind:activeTab/>
</div>

<style lang="scss">

  $GRID_SIZE: 10px;

  .starfield {
    position: absolute;
    inset: 0;
    pointer-events: none;

    .star {
      position: absolute;
      top: -8vh;
      border-radius: 999px;
      background: radial-gradient(circle at 35% 35%, #fff 0%, #d9f3ff 45%, rgba(255,255,255,0) 100%);
      animation: fall linear infinite;
    }
  }

  @keyframes fall {
    from { transform: translate3d(0, -10vh, 0); }
    to { transform: translate3d(-9vw, 110vh, 0); }
  }

  .tabbed-clickgui {
    background: radial-gradient(circle at 14% 30%, rgba(95, 224, 255, 0.26), transparent 35%),
      radial-gradient(circle at 86% 62%, rgba(245, 228, 104, 0.2), transparent 35%),
      var(--clickgui-overlay-background-color);
    overflow: hidden;
    position: absolute;
    will-change: opacity;
    transform-origin: top left;
    left: 0;
    top: 0;

    &.grid {
      background-image: linear-gradient(to right, var(--clickgui-grid-color) 1px, transparent 1px),
      linear-gradient(to bottom, var(--clickgui-grid-color) 1px, transparent 1px);
    }
  }
</style>
