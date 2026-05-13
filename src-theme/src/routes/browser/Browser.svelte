<script lang="ts">
    import type {Browser} from "../../integration/types.js";
    import {onMount} from "svelte";
    import {
        browserForceReload,
        browserGoBack,
        browserGoForward,
        browserNavigate,
        browserReload,
        getBrowser
    } from "../../integration/rest.js";
    import {listen} from "../../integration/ws";
    import type {BrowserUrlChangeEvent} from "../../integration/events";
    import {delay} from "../../integration/util";

    let browser: Browser;

    type Star = {
        left: number;
        size: number;
        duration: number;
        delay: number;
        opacity: number;
    };

    const stars: Star[] = Array.from({length: 60}, () => ({
        left: Math.random() * 100,
        size: Math.random() * 3 + 1,
        duration: Math.random() * 8 + 8,
        delay: Math.random() * -18,
        opacity: Math.random() * 0.7 + 0.25
    }));

    async function loadBrowser() {
        browser = await getBrowser();
    }

    onMount(async () => {
        await delay(250);
        await loadBrowser();
    });

    async function onKeyPress(event: KeyboardEvent) {
        if (event.key === "Enter") {
            await browserNavigate(browser.url);
        }
    }

    async function handleGo() {
        await browserNavigate(browser.url);
    }

    async function handleBack() {
        await browserGoBack();
    }

    async function handleForward() {
        await browserGoForward();
    }

    async function handleReload() {
        await browserReload();
    }

    async function handleForceReload() {
        await browserForceReload();
    }

    listen("browserUrlChange", (e: BrowserUrlChangeEvent) => {
        browser.url = e.url;
    });
</script>

<style>
    :global(body) {
        background: radial-gradient(circle at 20% 20%, #1f2f66 0%, #0c132f 40%, #050814 100%);
        color: #fff;
        overflow: hidden;
    }

    .hero {
        position: fixed;
        top: 24px;
        left: 24px;
        z-index: 3;
        pointer-events: none;
        text-shadow: 0 0 16px rgba(153, 219, 255, 0.7);
    }

    .hero h1 {
        margin: 0;
        font-size: clamp(1.75rem, 3vw, 2.7rem);
        letter-spacing: 0.12em;
        text-transform: uppercase;
    }

    .hero p {
        margin: 6px 0 0;
        font-size: 0.95rem;
        opacity: 0.85;
    }

    .starfield {
        position: fixed;
        inset: 0;
        pointer-events: none;
        z-index: 1;
    }

    .star {
        position: absolute;
        top: -8vh;
        border-radius: 50%;
        background: radial-gradient(circle at 35% 35%, #ffffff 0%, #cde7ff 50%, rgba(255, 255, 255, 0) 100%);
        animation-name: fall;
        animation-timing-function: linear;
        animation-iteration-count: infinite;
    }

    @keyframes fall {
        0% {
            transform: translate3d(0, -10vh, 0);
        }
        100% {
            transform: translate3d(-16vw, 115vh, 0);
        }
    }

    .browser-controls {
        display: flex;
        justify-content: space-between;
        align-items: center;
        position: fixed;
        bottom: 20px;
        left: 20px;
        right: 20px;
        background: rgba(15, 20, 45, 0.76);
        backdrop-filter: blur(10px);
        border: 1px solid rgba(255, 255, 255, 0.14);
        padding: 12px;
        border-radius: 14px;
        box-shadow: 0 10px 35px rgba(0, 0, 0, 0.35);
        z-index: 2;
    }

    .address-bar {
        flex-grow: 1;
        margin: 0 10px;
    }

    input {
        width: 100%;
        padding: 10px;
        border: 1px solid rgba(255, 255, 255, 0.22);
        border-radius: 8px;
        outline: none;
        font-size: 14px;
        color: white;
        background: rgba(7, 10, 20, 0.8);
    }

    button {
        background: linear-gradient(135deg, #52b2ff, #7c7cff);
        color: white;
        border: none;
        padding: 10px 15px;
        margin-left: 5px;
        border-radius: 8px;
        cursor: pointer;
        font-size: 14px;
        box-shadow: 0 4px 10px rgba(82, 178, 255, 0.35);
    }

    button:disabled {
        background: #6c757d;
        cursor: not-allowed;
    }

    button:focus {
        outline: none;
    }
</style>

<div class="starfield" aria-hidden="true">
    {#each stars as star}
        <span
            class="star"
            style="left: {star.left}%; width: {star.size}px; height: {star.size}px; opacity: {star.opacity}; animation-duration: {star.duration}s; animation-delay: {star.delay}s;"
        ></span>
    {/each}
</div>

<div class="hero">
    <h1>Shine Client</h1>
    <p>Super cool cosmic browser vibe</p>
</div>

{#if browser}
    <div class="browser-controls">
        <button on:click={handleBack}>&larr;</button>
        <button on:click={handleForward}>&rarr;</button>
        <button on:click={handleReload}>&#x21bb;</button>
        <div class="address-bar">
            <input id="url" bind:value={browser.url} on:keypress={onKeyPress} placeholder="Enter URL" />
        </div>
        <button on:click={handleGo}>Go</button>
        <button on:click={handleForceReload}>Force Reload</button>
    </div>
{/if}
