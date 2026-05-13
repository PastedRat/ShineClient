<script lang="ts">
    import type { Component } from "svelte";

    type Tab = {
        title: string;
        content: Component;
    };

    let { tabs, activeTab = $bindable(0) } = $props<{
        tabs: Tab[];
        activeTab?: number;
    }>();

    const Active = $derived(tabs[activeTab]?.content);
</script>

<div class="tabs">
    <div class="shine-brand"><span>★</span> Shine Client</div>
    <div class="available-tabs">
        {#each tabs as tab, index (tab.title)}
            <button
                    class="tab-button"
                    class:active={index === activeTab}
                    onclick={() => (activeTab = index)}
                    type="button"
            >
                {tab.title}
            </button>
        {/each}
    </div>

    <div class="content">
        {#if Active}
            {@render Active()}
        {/if}
    </div>
</div>

<style lang="scss">

    .shine-brand {
    position: fixed;
    top: 15px;
    left: 20px;
    z-index: 10000000000;
    font-size: 1.05rem;
    font-weight: 800;
    letter-spacing: 0.04em;
    color: #f7fbff;

    span {
      color: #f6e67e;
      margin-right: 8px;
      text-shadow: 0 0 12px rgba(246, 230, 126, 0.65);
    }
  }

  .available-tabs {
    position: fixed;
    top: 15px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 5px;
    padding: 6px;
    border-radius: 999px;
    background-color: var(--clickgui-tabs-background-color);
    box-shadow: 0 0 10px var(--clickgui-tabs-shadow-color);
    z-index: 9999999999;
  }

  .tab-button {
    background: transparent;
    color: var(--clickgui-text-dimmed-color);
    padding: 6px 14px;
    font-size: 14px;
    font-weight: 600;
    border-radius: 999px;
    cursor: pointer;
    transition: ease background-color 0.2s, ease color 0.2s;
    border: solid 1px transparent;

    &:hover {
      color: var(--clickgui-text-color);
      background-color: var(--clickgui-tab-hover-background-color);
    }

    &.active {
      color: var(--clickgui-text-color);
      background-color: var(--clickgui-tab-active-background-color);
      border: 1px solid var(--clickgui-tab-active-border-color);
    }
  }
</style>
