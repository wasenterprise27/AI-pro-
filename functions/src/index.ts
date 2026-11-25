import { enableFirebaseTelemetry } from '@genkit-ai/firebase';
import { genkit, z } from 'genkit';
import { googleAI, gemini15Flash } from '@genkit-ai/googleai';
import { onCallGenkit } from 'firebase-functions/https';
import * as admin from 'firebase-admin';

// Initialize Firebase OpenTelemetry tracing and metrics for Cloud Monitoring
enableFirebaseTelemetry();

if (!admin.apps.length) {
  admin.initializeApp();
}

// Initialize Genkit instance with Gemini models
export const ai = genkit({
  plugins: [googleAI()],
  model: gemini15Flash,
});

/**
 * Genkit Flow: Generate Multiplatform Viral Social Content
 */
export const generateViralContent = onCallGenkit(
  {
    authPolicy: (auth) => !!auth,
  },
  ai.defineFlow(
    {
      name: 'generateViralContent',
      inputSchema: z.object({
        topic: z.string(),
        platform: z.string().default('TikTok'),
        tone: z.string().default('Viral'),
      }),
      outputSchema: z.object({
        hooks: z.array(z.string()),
        caption: z.string(),
        hashtags: z.array(z.string()),
        cta: z.string(),
      }),
    },
    async (input) => {
      const response = await ai.generate({
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
    }
  )
);

/**
 * Genkit Flow: Generate Scene-by-Scene Video Script
 */
export const generateVideoScript = onCallGenkit(
  {
    authPolicy: (auth) => !!auth,
  },
  ai.defineFlow(
    {
      name: 'generateVideoScript',
      inputSchema: z.object({
        topic: z.string(),
        durationSeconds: z.number().default(30),
      }),
      outputSchema: z.object({
        title: z.string(),
        scenes: z.array(
          z.object({
            sceneNumber: z.number(),
            durationSeconds: z.number(),
            visualDescription: z.string(),
            voiceoverText: z.string(),
            onScreenText: z.string(),
          })
        ),
      }),
    },
    async (input) => {
      const prompt = `Write a viral ${input.durationSeconds}-second short-form video script about "${input.topic}".
Include shot directions, punchy voiceover lines, and on-screen text directives.`;

      const response = await ai.generate({ prompt });

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
    }
  )
);
