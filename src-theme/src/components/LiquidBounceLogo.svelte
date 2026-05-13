<script lang="ts">
    export let width = "261.263px";
    export let height = "98px";
    export let badgeFill = "var(--accent-color)";
    export let badgeTextFill = "var(--text-color)";
    export let badgeGroupClass = "";
</script>

<div class="shine-logo {badgeGroupClass}" style={`width:${width};height:${height};`} aria-label="Shine Client logo">
    <div class="mark">★</div>
    <div class="wordmark">
        <div class="title">Shine Client</div>
        <div class="badge" style={`background:${badgeFill};color:${badgeTextFill};`}>Premium</div>
    </div>
</div>

<style>
    .shine-logo {
        display: flex;
        align-items: center;
        gap: 12px;
    }

    .mark {
        width: 40%;
        max-width: 86px;
        min-width: 40px;
        aspect-ratio: 1;
        border-radius: 14px;
        display: grid;
        place-items: center;
        background: radial-gradient(circle at 30% 30%, #fff5a1 0%, #f4df71 55%, #70dbe9 100%);
        color: #061124;
        font-size: clamp(1.35rem, 3.2vw, 2.5rem);
        font-weight: 900;
        box-shadow: 0 0 16px rgba(244, 224, 108, 0.35);
    }

    .wordmark { display: flex; flex-direction: column; justify-content: center; gap: 8px; }

    .title {
        font-size: clamp(1.05rem, 2.1vw, 2rem);
        line-height: 1;
        font-weight: 800;
        color: #fff;
        letter-spacing: 0.02em;
    }

    .badge {
        width: fit-content;
        border-radius: 999px;
        padding: 0.25rem 0.72rem;
        font-size: clamp(0.58rem, 1vw, 0.75rem);
        font-weight: 700;
    }
</style>
