<script lang="ts">
    const stars = Array.from({length: 36}, (_, index) => ({
        index,
        left: `${(index * 37) % 100}%`,
        delay: `${(index % 12) * 0.35}s`,
        duration: `${6 + (index % 7) * 0.8}s`,
        size: `${1 + (index % 3)}px`,
        opacity: 0.3 + (index % 5) * 0.12
    }));
</script>

<div class="stars" aria-hidden="true">
    {#each stars as star (star.index)}
        <span
            class="star"
            style={`left: ${star.left}; animation-delay: ${star.delay}; animation-duration: ${star.duration}; width: ${star.size}; height: ${star.size}; opacity: ${star.opacity};`}
        ></span>
    {/each}
</div>

<style>
    .stars {
        position: absolute;
        inset: 0;
        overflow: hidden;
        pointer-events: none;
        z-index: -2;
        background:
            radial-gradient(circle at 20% 20%, rgb(80 50 170 / 35%), transparent 50%),
            radial-gradient(circle at 80% 10%, rgb(30 130 255 / 28%), transparent 40%),
            linear-gradient(180deg, rgb(11 16 33 / 95%), rgb(4 8 19 / 96%));
    }

    .star {
        position: absolute;
        top: -8%;
        border-radius: 999px;
        background: #fff;
        box-shadow: 0 0 8px rgb(150 220 255 / 80%);
        animation: fall linear infinite;
    }

    .star::after {
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
</style>
