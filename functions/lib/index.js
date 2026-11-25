"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateVideoScript = exports.generateViralContent = exports.ai = void 0;
const firebase_1 = require("@genkit-ai/firebase");
const genkit_1 = require("genkit");
const googleai_1 = require("@genkit-ai/googleai");
const https_1 = require("firebase-functions/https");
const admin = __importStar(require("firebase-admin"));
// Initialize Firebase OpenTelemetry tracing and metrics for Cloud Monitoring
(0, firebase_1.enableFirebaseTelemetry)();
if (!admin.apps.length) {
    admin.initializeApp();
}
// Initialize Genkit instance with Gemini models
exports.ai = (0, genkit_1.genkit)({
    plugins: [(0, googleai_1.googleAI)()],
    model: googleai_1.gemini15Flash,
});
/**
 * Genkit Flow: Generate Multiplatform Viral Social Content
 */
exports.generateViralContent = (0, https_1.onCallGenkit)({
    authPolicy: (auth) => !!auth,
}, exports.ai.defineFlow({
    name: 'generateViralContent',
    inputSchema: genkit_1.z.object({
        topic: genkit_1.z.string(),
        platform: genkit_1.z.string().default('TikTok'),
        tone: genkit_1.z.string().default('Viral'),
    }),
    outputSchema: genkit_1.z.object({
        hooks: genkit_1.z.array(genkit_1.z.string()),
        caption: genkit_1.z.string(),
        hashtags: genkit_1.z.array(genkit_1.z.string()),
        cta: genkit_1.z.string(),
    }),
}, async (input) => {
    const response = await exports.ai.generate({
        prompt: `You are an expert social media copywriter.
Target Platform: ${input.platform}
Tone: ${input.tone}
Topic: ${input.topic}

Generate:
1. 3 high-retention pattern-interrupt hooks.
2. A viral caption optimized for engagement.
3. 5 trending, niche-specific hashtags.
4. A high-converting call to action (CTA).`,
    });
    return {
        hooks: [
            `Stop scrolling if you care about ${input.topic}!`,
            `The biggest secret about ${input.topic} in 2026...`,
            `Why 99% of people fail at ${input.topic} (and how to win).`,
        ],
        caption: response.text,
        hashtags: [`#${input.platform}`, '#ViralContent', '#GrowthTips', '#CreatorEconomy'],
        cta: 'Save this post and comment your favorite tip below!',
    };
}));
/**
 * Genkit Flow: Generate Scene-by-Scene Video Script
 */
exports.generateVideoScript = (0, https_1.onCallGenkit)({
    authPolicy: (auth) => !!auth,
}, exports.ai.defineFlow({
    name: 'generateVideoScript',
    inputSchema: genkit_1.z.object({
        topic: genkit_1.z.string(),
        durationSeconds: genkit_1.z.number().default(30),
    }),
    outputSchema: genkit_1.z.object({
        title: genkit_1.z.string(),
        scenes: genkit_1.z.array(genkit_1.z.object({
            sceneNumber: genkit_1.z.number(),
            durationSeconds: genkit_1.z.number(),
            visualDescription: genkit_1.z.string(),
            voiceoverText: genkit_1.z.string(),
            onScreenText: genkit_1.z.string(),
        })),
    }),
}, async (input) => {
    const prompt = `Write a viral ${input.durationSeconds}-second short-form video script about "${input.topic}".
Include shot directions, punchy voiceover lines, and on-screen text directives.`;
    const response = await exports.ai.generate({ prompt });
    return {
        title: `Viral Script: ${input.topic}`,
        scenes: [
            {
                sceneNumber: 1,
                durationSeconds: 3,
                visualDescription: 'Fast zoom-in on presenter with energetic expression, holding prop.',
                voiceoverText: `Wait, don't scroll! You need to hear this about ${input.topic}.`,
                onScreenText: `WAIT! 🚨 ${input.topic.toUpperCase()}`,
            },
            {
                sceneNumber: 2,
                durationSeconds: 15,
                visualDescription: 'B-roll showcase cut with rapid transitions and highlighted pointers.',
                voiceoverText: response.text.slice(0, 200),
                onScreenText: 'KEY STRATEGY 💡',
            },
            {
                sceneNumber: 3,
                durationSeconds: 12,
                visualDescription: 'Host back on camera pointing down to comment button.',
                voiceoverText: 'Hit follow for part 2 and tap the link in bio to get our template!',
                onScreenText: 'FOLLOW FOR MORE 🚀',
            },
        ],
    };
}));
//# sourceMappingURL=index.js.map