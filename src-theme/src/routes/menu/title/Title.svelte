<script lang="ts">
    import MainButton from "./buttons/MainButton.svelte";
    import ChildButton from "./buttons/ChildButton.svelte";
    import ConfettiBackground from "./ConfettiBackground.svelte";
    import ButtonContainer from "../common/buttons/ButtonContainer.svelte";
    import IconTextButton from "../common/buttons/IconTextButton.svelte";
    import {
        exitClient,
        getClientUpdate,
        openScreen,
        toggleBackgroundShaderEnabled
    } from "../../../integration/rest";
    import Menu from "../common/Menu.svelte";
    import {fly} from "svelte/transition";
    import {onMount} from "svelte";
    import {notification} from "../common/header/notification_store";
    import {isAnniversary} from "../../../util/utils";

    const titleStars = Array.from({length: 36}, (_, index) => ({
        index,
        left: `${(index * 37) % 100}%`,
        delay: `${(index % 12) * 0.35}s`,
        duration: `${6 + (index % 7) * 0.8}s`,
        size: `${1 + (index % 3)}px`,
        opacity: 0.3 + (index % 5) * 0.12
    }));

    let regularButtonsShown = true;
    let clientButtonsShown = false;

    onMount(() => {
        setTimeout(async () => {
            const clientUpdate = await getClientUpdate();

            if (clientUpdate.update) {
                notification.set({
                    title: `Shine Client ${clientUpdate.update.clientVersion} has been released!`,
                    message: `Download it from shineclient.net!`,
                    error: false,
                    delay: 99999999
                });
            }
        }, 2000);
    });

    function toggleButtons() {
        if (clientButtonsShown) {
            clientButtonsShown = false;
            setTimeout(() => {
                regularButtonsShown = true;
            }, 750);
        } else {
            regularButtonsShown = false;
            setTimeout(() => {
                clientButtonsShown = true;
            }, 750);
        }
    }
</script>

<div class="title-screen">
    <div class="falling-stars" aria-hidden="true">
        {#each titleStars as star (star.index)}
            <span
                class="falling-star"
                style={`left: ${star.left}; animation-delay: ${star.delay}; animation-duration: ${star.duration}; width: ${star.size}; height: ${star.size}; opacity: ${star.opacity};`}
            ></span>
        {/each}
    </div>

    {#if isAnniversary()}
        <ConfettiBackground />
    {/if}

    <Menu>
        <div class="content">
            <div class="shine-title" transition:fly|global={{duration: 700, y: -50}}>
                <h1>Shine Client</h1>
                <p>Launch brighter. Play sharper.</p>
            </div>
            <div class="main-buttons">
                {#if regularButtonsShown}
                    <MainButton title="Singleplayer" icon="singleplayer" index={0}
                                on:click={() => openScreen("singleplayer")}/>

                    <MainButton title="Multiplayer" icon="multiplayer" let:parentHovered
                                on:click={() => openScreen("multiplayer")} index={1}>
                        <ChildButton title="Realms" icon="realms" {parentHovered}
                                     on:click={() => openScreen("multiplayer_realms")}/>
                    </MainButton>
                    <MainButton title="Shine Client" icon="star" on:click={toggleButtons} index={2}/>
                    <MainButton title="Options" icon="options" on:click={() => openScreen("options")} index={3}/>
                {:else if clientButtonsShown}
                    <MainButton title="Proxy Manager" icon="proxymanager" on:click={() => openScreen("proxymanager")}
                                index={0}/>
                    <MainButton title="Click GUI" icon="clickgui" on:click={() => openScreen("clickgui")} index={1}/>
                    <!-- <MainButton title="Scripts" icon="scripts" index={2}/> -->
                    <MainButton title="Back" icon="back-large" on:click={toggleButtons} index={2}/>
                {/if}
            </div>

            <div class="additional-buttons" transition:fly|global={{duration: 700, y: 100}}>
                <ButtonContainer>
                    <IconTextButton icon="icon-exit.svg" title="Exit" on:click={exitClient}/>
                    <IconTextButton icon="icon-change-background.svg" title="Toggle Shader"
                                    on:click={toggleBackgroundShaderEnabled}/>
                </ButtonContainer>
            </div>

        </div>
    </Menu>
</div>

<style>
    .title-screen {
        position: relative;
        isolation: isolate;
        overflow: hidden;
    }

    .title-screen::before {
        content: "";
        position: absolute;
        inset: 0;
        z-index: -2;
        background: radial-gradient(circle at 12% 30%, rgba(100, 231, 255, 0.25), transparent 36%),
            radial-gradient(circle at 88% 58%, rgba(247, 229, 119, 0.2), transparent 30%),
            linear-gradient(125deg, #01040f 0%, #020819 45%, #01040c 100%);
    }

    .title-screen::after {
        content: "";
        position: absolute;
        inset: 0;
        z-index: -1;
        background-image: radial-gradient(circle, rgba(255,255,255,0.8) 1.1px, transparent 1.2px);
        background-size: 52px 52px;
        opacity: 0.45;
    }


    .falling-stars {
        position: absolute;
        inset: 0;
        overflow: hidden;
        pointer-events: none;
        z-index: -1;
    }

    .falling-star {
        position: absolute;
        top: -8%;
        border-radius: 999px;
        background: #fff;
        box-shadow: 0 0 8px rgb(150 220 255 / 80%);
        animation: fall linear infinite;
    }

    .falling-star::after {
        content: "";
        position: absolute;
        right: 0;
        top: 50%;
        width: 44px;
        height: 1px;
        transform: translate(100%, -50%);
        background: linear-gradient(90deg, rgb(180 230 255 / 70%), transparent);
    }

    @keyframes fall {
        from {
            transform: translate3d(0, -12vh, 0);
        }
        to {
            transform: translate3d(-15vw, 120vh, 0);
        }
    }

    .content {
        flex: 1;
        display: grid;
        grid-template-areas:
            "title"
            "a"
            "b";
        grid-template-rows: max-content 1fr max-content;
        grid-template-columns: 1fr;
        row-gap: 24px;
    }

    .shine-title {
        grid-area: title;
        display: flex;
        flex-direction: column;
        gap: 6px;
        text-transform: uppercase;
        letter-spacing: 0.2em;
    }

    .shine-title h1 {
        margin: 0;
        font-size: clamp(2.2rem, 5vw, 4.6rem);
        font-weight: 800;
        color: #f5fbff;
        text-shadow: 0 0 24px rgb(120 200 255 / 75%);
    }

    .shine-title p {
        margin: 0;
        font-size: 0.92rem;
        color: rgb(190 220 255 / 90%);
    }

    .main-buttons {
        display: flex;
        flex-direction: column;
        row-gap: 25px;
        grid-area: a;
    }

    .additional-buttons {
        grid-area: b;
    }

</style>
