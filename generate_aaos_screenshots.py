#!/usr/bin/env python3
"""
Android Automotive OS (AAOS) Screenshot Generator
Generates 8 Google Play Store compliant screenshots:
- 4 Landscape: 1024 px by 768 px
- 4 Portrait: 800 px by 1280 px
- Formats: PNG, well under 8 MB each.
"""

import os
import subprocess

OUTPUT_DIRS = [
    "automotive_screenshots",
    ".aistudio/artifacts/brain/844a5107-0f2d-4e37-99c9-96ddf92d8d2b"
]

for d in OUTPUT_DIRS:
    os.makedirs(d, exist_ok=True)

def run_magick(args, outfile):
    for d in OUTPUT_DIRS:
        dest = os.path.join(d, outfile)
        cmd = ["convert"] + args + [dest]
        res = subprocess.run(cmd, capture_output=True, text=True)
        if res.returncode != 0:
            print(f"Error generating {dest}: {res.stderr}")
        else:
            print(f"Generated {dest}")

def generate_landscape_1():
    # 1024 x 768 - Dashboard & Voice Quick Actions
    args = [
        "-size", "1024x768", "xc:#070b14",
        # Top AAOS Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 1024,48",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 24,30 '10:42 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 100,30 'PRND [D]'",
        "-fill", "#10b981", "-draw", "text 200,30 'EV 84% • 312 mi'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 860,30 '5G LTE  |  Driver: Stephen  [o]'",
        
        # Left Automotive Navigation Rail (AAOS standard 80px)
        "-fill", "#0c1322", "-draw", "rectangle 0,48 88,712",
        "-fill", "#6366f1", "-draw", "roundrectangle 14,70 74,130 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 32,106 'AI'",
        "-fill", "#475569", "-draw", "roundrectangle 14,150 74,200 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,180 'Home'",
        "-fill", "#475569", "-draw", "roundrectangle 14,215 74,265 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,245 'Video'",
        "-fill", "#475569", "-draw", "roundrectangle 14,280 74,330 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,310 'Audio'",
        "-fill", "#475569", "-draw", "roundrectangle 14,345 74,395 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 22,375 'Monet'",
        "-fill", "#475569", "-draw", "roundrectangle 14,410 74,460 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,440 'Tools'",

        # Header Title in content area
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 112,88 'AI pro 001 — In-Car Creator Hub'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 112,112 'Hands-free voice creation & viral multi-platform publisher for Automotive OS'",

        # Big Hero Card - Voice Dictation & Quick Generator
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 112,130 996,300 16,16",
        "-stroke", "none",
        "-fill", "#10b981", "-draw", "roundrectangle 136,152 236,182 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 146,172 'VOICE READY'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 256,172 '\"Create 3 viral TikTok hooks for new electric car gadgets\"'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 136,212 'Tap the microphone or use steering wheel voice button to dictate prompts while parked or charging.'",
        
        # Action Buttons in Hero
        "-fill", "#6366f1", "-draw", "roundrectangle 136,236 330,284 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 160,266 '[Mic] Start Voice Dictation'",
        "-fill", "#1e293b", "-draw", "roundrectangle 346,236 510,284 10,10",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 372,266 'Generate Scripts'",

        # Metric Cards (3 Columns)
        # Card 1: Viral Views
        "-fill", "#111827", "-stroke", "#1e293b", "-strokewidth", "1",
        "-draw", "roundrectangle 112,320 390,440 14,14",
        "-stroke", "none",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 132,348 'TOTAL VIRAL VIEWS'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "28",
        "-draw", "text 132,392 '1.42 M'",
        "-fill", "#10b981", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 132,422 '+24.8% this week across TikTok & Shorts'",

        # Card 2: Hook Retention
        "-fill", "#111827", "-stroke", "#1e293b", "-strokewidth", "1",
        "-draw", "roundrectangle 414,320 692,440 14,14",
        "-stroke", "none",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 434,348 'AVG HOOK RETENTION'",
        "-fill", "#a855f7", "-font", "DejaVu-Sans-Bold", "-pointsize", "28",
        "-draw", "text 434,392 '89.4%'",
        "-fill", "#10b981", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 434,422 'Pattern-interrupt hooks scoring 94/100'",

        # Card 3: In-Car Ad & Credit Balance
        "-fill", "#111827", "-stroke", "#1e293b", "-strokewidth", "1",
        "-draw", "roundrectangle 716,320 996,440 14,14",
        "-stroke", "none",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 736,348 'AI CREDITS & AD REVENUE'",
        "-fill", "#10b981", "-font", "DejaVu-Sans-Bold", "-pointsize", "28",
        "-draw", "text 736,392 '$1,428.50'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 736,422 '95 Credits Available • AdMob Connected'",

        # Recent Content Generated Row
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 112,475 'Recent In-Car Drafts & Ready-to-Post Drops'",
        
        # Item 1
        "-fill", "#1e293b", "-draw", "roundrectangle 112,492 540,580 12,12",
        "-fill", "#ec4899", "-draw", "roundrectangle 128,508 200,532 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 138,525 'TIKTOK'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 212,525 'Stop charging your phone wrong in your car!'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 128,555 'Viral Hook #1  •  3.4k predicted shares  •  15s Ad script ready'",
        "-fill", "#10b981", "-draw", "roundrectangle 450,515 526,555 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 465,540 'Copy'",

        # Item 2
        "-fill", "#1e293b", "-draw", "roundrectangle 568,492 996,580 12,12",
        "-fill", "#ef4444", "-draw", "roundrectangle 584,508 668,532 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 594,525 'YOUTUBE'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 680,525 'Top 5 AI tools every creator must have in 2026'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 584,555 '60s Short Script  •  SEO tags included  •  High CTR Hooks'",
        "-fill", "#10b981", "-draw", "roundrectangle 906,515 982,555 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 921,540 'Copy'",

        # Bottom Automotive HVAC Bar (AAOS Standard 56px)
        "-fill", "#050912", "-draw", "rectangle 0,712 1024,768",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 36,746 '[Heat] 70°F'",
        "-fill", "#64748b", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 160,746 'Seat Warmer: [3]'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 420,746 'AUTO CLIMATE  |  FAN [|||| ]  |  DEFROST'",
        "-fill", "#64748b", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 760,746 'Seat Warmer: [OFF]'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 920,746 '72°F [Sync]'",
    ]
    run_magick(args, "screenshot_1_landscape_1024x768.png")

def generate_landscape_2():
    # 1024 x 768 - AI Video Script Studio & Teleprompter
    args = [
        "-size", "1024x768", "xc:#070b14",
        # Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 1024,48",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 24,30 '10:45 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 100,30 'PRND [P] PARKED'",
        "-fill", "#10b981", "-draw", "text 230,30 'EV 84% • SAFE FOR PRODUCTION'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 860,30 '5G LTE  |  Teleprompter Live'",

        # Left Nav Rail
        "-fill", "#0c1322", "-draw", "rectangle 0,48 88,712",
        "-fill", "#475569", "-draw", "roundrectangle 14,70 74,120 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,100 'Home'",
        "-fill", "#6366f1", "-draw", "roundrectangle 14,135 74,185 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 24,165 'Video'",
        "-fill", "#475569", "-draw", "roundrectangle 14,200 74,250 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,230 'Audio'",
        "-fill", "#475569", "-draw", "roundrectangle 14,265 74,315 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,295 'Monet'",

        # Main Workspace Title
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 112,85 'AI Video Script Studio & Automotive Teleprompter'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 112,108 'Script: \"The 3-Second Rule That Skyrocketed My Engagement\"  •  15s TikTok & YouTube Shorts'",

        # Left Script Panel (Scene Breakdown)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 112,125 560,590 16,16",
        "-stroke", "none",
        "-fill", "#a855f7", "-draw", "roundrectangle 132,145 220,173 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 142,164 'SCENE 1 (0-3s)'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 236,164 'Visual: Fast zoom-in pointing at camera'",
        "-fill", "#e2e8f0", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 132,198 'VOICEOVER: \"Stop scrolling if your content gets 200 views!\"'",

        "-fill", "#38bdf8", "-draw", "roundrectangle 132,230 220,258 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 142,249 'SCENE 2 (3-9s)'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 236,249 'Visual: Show analytics graph spike on phone'",
        "-fill", "#e2e8f0", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 132,283 'VOICEOVER: \"Here is the exact formula top creators use daily:\"'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 132,308 '1. Pattern-interrupt question  2. Value bomb in 5 seconds'",

        "-fill", "#10b981", "-draw", "roundrectangle 132,340 220,368 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 142,359 'SCENE 3 (9-15s)'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 236,359 'Visual: Text overlay with free download link'",
        "-fill", "#e2e8f0", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 132,393 'VOICEOVER: \"Comment SCRIPT below and I will DM you the template!\"'",

        "-fill", "#6366f1", "-draw", "roundrectangle 132,440 330,490 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 155,471 '[Play] Read Aloud (TTS)'",
        "-fill", "#1e293b", "-draw", "roundrectangle 346,440 540,490 10,10",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 375,471 '[Edit] Regenerate Scene'",

        # Right Teleprompter Display
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 584,125 996,590 16,16",
        "-stroke", "none",
        "-fill", "#ef4444", "-draw", "roundrectangle 608,145 740,175 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 620,165 'TELEPROMPTER'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 756,165 'Font Size: 32pt  •  Speed: 1.2x'",

        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 608,230 'STOP SCROLLING IF YOUR'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 608,270 'CONTENT GETS 200 VIEWS!'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "18",
        "-draw", "text 608,330 'Here is the exact formula that'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "18",
        "-draw", "text 608,370 'top creators use daily to trigger'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "18",
        "-draw", "text 608,410 'the viral algorithm...'",

        "-fill", "#ef4444", "-draw", "roundrectangle 608,490 790,550 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 640,526 '[REC] START RECORDING'",
        "-fill", "#334155", "-draw", "roundrectangle 806,490 976,550 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 835,526 'PAUSE SCROLL'",

        # Bottom HVAC
        "-fill", "#050912", "-draw", "rectangle 0,712 1024,768",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 36,746 '[Heat] 70°F'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 420,746 'AUTO CLIMATE  |  FAN [|||| ]  |  DEFROST'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 920,746 '72°F [Sync]'",
    ]
    run_magick(args, "screenshot_2_landscape_1024x768.png")

def generate_landscape_3():
    # 1024 x 768 - Audio Narrator & Multi-Platform Calendar
    args = [
        "-size", "1024x768", "xc:#070b14",
        # Top Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 1024,48",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 24,30 '10:50 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 100,30 'PRND [D] CRUISE'",
        "-fill", "#10b981", "-draw", "text 230,30 'EV 83% • MEDIA STREAMING'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 860,30 '5G LTE  |  Audio Playback Active'",

        # Left Nav Rail
        "-fill", "#0c1322", "-draw", "rectangle 0,48 88,712",
        "-fill", "#475569", "-draw", "roundrectangle 14,70 74,120 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,100 'Home'",
        "-fill", "#475569", "-draw", "roundrectangle 14,135 74,185 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,165 'Video'",
        "-fill", "#6366f1", "-draw", "roundrectangle 14,200 74,250 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 24,230 'Audio'",
        "-fill", "#475569", "-draw", "roundrectangle 14,265 74,315 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,295 'Monet'",

        # Title
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 112,85 'In-Car AI Audio Narrator & Multi-Channel Calendar'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 112,108 'Review voiceover drafts over vehicle speakers and plan upcoming drops'",

        # Media Player Card (Left)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 112,125 540,590 16,16",
        "-stroke", "none",
        "-fill", "#6366f1", "-draw", "roundrectangle 136,150 250,180 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 148,170 'TTS NARRATOR'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 136,215 'Voice: Nova (Energetic Tech)'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 136,240 'Track: \"5 Secret AI Prompts for Faster Editing\"'",

        # Scrubber bar
        "-fill", "#334155", "-draw", "roundrectangle 136,270 516,280 4,4",
        "-fill", "#6366f1", "-draw", "roundrectangle 136,270 340,280 4,4",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 136,305 '0:34 / 1:15'",
        "-fill", "#10b981", "-draw", "text 460,305 'HQ 320kbps'",

        # Audio Player Controls
        "-fill", "#1e293b", "-draw", "roundrectangle 136,330 206,380 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 155,360 '[-15s]'",
        "-fill", "#6366f1", "-draw", "roundrectangle 226,325 316,385 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 246,360 '[ || ]'",
        "-fill", "#1e293b", "-draw", "roundrectangle 336,330 406,380 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 355,360 '[+15s]'",
        "-fill", "#1e293b", "-draw", "roundrectangle 426,330 516,380 10,10",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 446,360 '1.25x Speed'",

        # Vehicle Audio Route
        "-fill", "#10b981", "-draw", "roundrectangle 136,410 516,460 10,10",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 160,440 '[Audio System] Connected: Vehicle Premium Sound'",

        # Multi-Channel Calendar (Right)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 564,125 996,590 16,16",
        "-stroke", "none",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 588,160 '📅 Scheduled Drops Across Social Channels'",

        # Event 1
        "-fill", "#1e293b", "-draw", "roundrectangle 588,185 972,255 10,10",
        "-fill", "#ec4899", "-draw", "roundrectangle 604,200 670,222 4,4",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "10",
        "-draw", "text 612,216 'TIKTOK'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 680,216 'Today at 6:30 PM (Peak Viral Slot)'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 604,242 'Status: Ready to auto-publish • 15s Ad Hook'",

        # Event 2
        "-fill", "#1e293b", "-draw", "roundrectangle 588,270 972,340 10,10",
        "-fill", "#ef4444", "-draw", "roundrectangle 604,285 684,307 4,4",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "10",
        "-draw", "text 612,301 'YOUTUBE'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 694,301 'Tomorrow at 12:00 PM (Lunch Break)'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 604,327 'Status: Script Approved • Drive Asset Attached'",

        # Event 3
        "-fill", "#1e293b", "-draw", "roundrectangle 588,355 972,425 10,10",
        "-fill", "#8b5cf6", "-draw", "roundrectangle 604,370 690,392 4,4",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "10",
        "-draw", "text 612,386 'INSTAGRAM'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 700,386 'Wednesday at 7:00 PM (Reel Drop)'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 604,412 'Status: Generated Sales Copy + 15 Hashtags'",

        # Schedule Button
        "-fill", "#6366f1", "-draw", "roundrectangle 588,450 972,500 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 710,480 '[+] Add New Scheduled Slot'",

        # Bottom HVAC
        "-fill", "#050912", "-draw", "rectangle 0,712 1024,768",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 36,746 '[Heat] 70°F'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 420,746 'AUTO CLIMATE  |  FAN [|||| ]  |  DEFROST'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 920,746 '72°F [Sync]'",
    ]
    run_magick(args, "screenshot_3_landscape_1024x768.png")

def generate_landscape_4():
    # 1024 x 768 - AdMob Monetization & Affiliate Copy Suite
    args = [
        "-size", "1024x768", "xc:#070b14",
        # Top Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 1024,48",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 24,30 '10:55 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 100,30 'PRND [P] SUPERCHARGER'",
        "-fill", "#10b981", "-draw", "text 280,30 'EV 83% • MONETIZATION SUITE'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 860,30 '5G LTE  |  AdMob Connected'",

        # Left Nav Rail
        "-fill", "#0c1322", "-draw", "rectangle 0,48 88,712",
        "-fill", "#475569", "-draw", "roundrectangle 14,70 74,120 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,100 'Home'",
        "-fill", "#475569", "-draw", "roundrectangle 14,135 74,185 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,165 'Video'",
        "-fill", "#475569", "-draw", "roundrectangle 14,200 74,250 10,10",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 24,230 'Audio'",
        "-fill", "#10b981", "-draw", "roundrectangle 14,265 74,315 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 24,295 'Monet'",

        # Title
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 112,85 'Google AdMob Monetization & Affiliate Assistant'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 112,108 'Track ad revenue, watch rewarded ads while charging, and build Amazon sales copy'",

        # AdMob Network Status Card (Left)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 112,125 540,590 16,16",
        "-stroke", "none",
        "-fill", "#10b981", "-draw", "roundrectangle 136,150 250,180 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 148,170 'ADMOB ACTIVE'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 264,170 'SDK v23.6.0 Live Status'",

        # Metrics
        "-fill", "#1e293b", "-draw", "roundrectangle 136,200 320,270 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 152,225 'Est. Ad Revenue'",
        "-fill", "#10b981", "-font", "DejaVu-Sans-Bold", "-pointsize", "20",
        "-draw", "text 152,255 '$1,428.50'",

        "-fill", "#1e293b", "-draw", "roundrectangle 336,200 516,270 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 352,225 'Live eCPM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "20",
        "-draw", "text 352,255 '$14.80'",

        # Rewarded Ad Section
        "-fill", "#1e293b", "-draw", "roundrectangle 136,290 516,420 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 156,325 'Watch Rewarded Video Ad (+5 AI Credits)'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 156,350 'Earn free premium generation credits while your vehicle is parked.'",
        "-fill", "#10b981", "-draw", "roundrectangle 156,365 496,410 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 210,393 '[Play Video Ad] Claim +5 Free Credits'",

        # AdMob Native Preview
        "-fill", "#0f172a", "-draw", "roundrectangle 136,440 516,560 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 152,465 'SPONSORED BY GOOGLE ADMOB NETWORK'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 152,495 'High-Converting Digital Products for 2026'",
        "-fill", "#6366f1", "-draw", "roundrectangle 152,515 280,548 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 175,537 'Install App'",

        # Affiliate Marketing Assistant (Right)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 564,125 996,590 16,16",
        "-stroke", "none",
        "-fill", "#f59e0b", "-draw", "roundrectangle 588,150 720,180 6,6",
        "-fill", "#451a03", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 600,170 'AMAZON AFFILIATE'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 736,170 'Product Link Copywriter'",

        "-fill", "#1e293b", "-draw", "roundrectangle 588,200 972,250 8,8",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 604,230 'URL: amazon.com/dp/B08X9... (Wireless MagSafe Car Mount)'",

        "-fill", "#10b981", "-draw", "roundrectangle 588,265 800,310 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 615,293 '[Auto] Analyze & Build Sales Copy'",

        # Output Box
        "-fill", "#0f172a", "-draw", "roundrectangle 588,330 972,560 12,12",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 608,360 'TIKTOK VIRAL HOOK:'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 608,385 '\"If you drive and use your phone for GPS, throw away your old mount!\"'",
        "-fill", "#ec4899", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 608,425 'SALES COPY (INSTAGRAM/FB):'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 608,450 'Upgrade your dashboard! 15W fast charge, 360-degree rotation.'",
        "-fill", "#10b981", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 608,490 'SEO KEYWORDS & AFFILIATE TAGS:'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "11",
        "-draw", "text 608,515 'magsafe car mount, tesla tech accessories #CommissionsEarned'",

        # Bottom HVAC
        "-fill", "#050912", "-draw", "rectangle 0,712 1024,768",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 36,746 '[Heat] 70°F'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 420,746 'AUTO CLIMATE  |  FAN [|||| ]  |  DEFROST'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 920,746 '72°F [Sync]'",
    ]
    run_magick(args, "screenshot_4_landscape_1024x768.png")

def generate_portrait_5():
    # 800 x 1280 - Vertical AAOS Center Console: AI Viral Content Generator
    args = [
        "-size", "800x1280", "xc:#070b14",
        # Top AAOS Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 800,56",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 28,36 '10:42 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 120,36 'PRND [D]'",
        "-fill", "#10b981", "-draw", "text 220,36 'EV 84% • 312 mi'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 600,36 '5G LTE  |  Stephen [o]'",

        # App Header
        "-fill", "#6366f1", "-draw", "roundrectangle 28,80 96,148 14,14",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 44,124 'AI'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "26",
        "-draw", "text 116,114 'AI pro 001 — ContentGen'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 116,142 'Automotive Center Display • AI Content Suite'",

        # Platform Selector Pills
        "-fill", "#ec4899", "-draw", "roundrectangle 28,175 180,225 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 56,206 'TikTok'",
        "-fill", "#1e293b", "-draw", "roundrectangle 195,175 370,225 12,12",
        "-fill", "#ef4444", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 218,206 'YouTube Shorts'",
        "-fill", "#1e293b", "-draw", "roundrectangle 385,175 550,225 12,12",
        "-fill", "#a855f7", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 410,206 'Instagram'",
        "-fill", "#1e293b", "-draw", "roundrectangle 565,175 720,225 12,12",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 595,206 'Facebook'",

        # Topic Input Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,245 772,425 16,16",
        "-stroke", "none",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 52,280 'CONTENT TOPIC OR PRODUCT URL'",
        "-fill", "#1e293b", "-draw", "roundrectangle 52,295 748,355 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans", "-pointsize", "16",
        "-draw", "text 68,333 '\"Why 90% of creators fail before their 10th video\"'",
        
        "-fill", "#6366f1", "-draw", "roundrectangle 52,370 380,412 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 75,397 '[Mic] Dictate Hands-Free'",
        "-fill", "#10b981", "-draw", "roundrectangle 396,370 748,412 10,10",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 470,397 '⚡ Generate Viral Hooks'",

        # Generated Results Header & Viral Gauge
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "20",
        "-draw", "text 28,465 'Viral Hooks & Conversion Scores'",
        
        # Viral Score Badge
        "-fill", "#10b981", "-draw", "roundrectangle 570,442 772,482 10,10",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 590,468 'Viral Score: 94/100'",

        # Hook 1 Card (Pattern Interrupt)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,500 772,625 16,16",
        "-stroke", "none",
        "-fill", "#ec4899", "-draw", "roundrectangle 48,518 180,546 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 60,537 'HOOK #1 (PATTERN INTERRUPT)'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 48,574 '\"If you think video editing takes 10 hours, you are being lied to.\"'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 48,605 'Estimated retention: 91%  •  3.8k predicted shares'",
        "-fill", "#10b981", "-draw", "roundrectangle 670,570 752,610 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 688,595 'Copy'",

        # Hook 2 Card (Curiosity Gap)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,640 772,765 16,16",
        "-stroke", "none",
        "-fill", "#38bdf8", "-draw", "roundrectangle 48,658 180,686 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 60,677 'HOOK #2 (CURIOSITY GAP)'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 48,714 '\"The secret reason why nobody finishes watching your Reels...\"'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 48,745 'Estimated retention: 88%  •  High comment trigger'",
        "-fill", "#10b981", "-draw", "roundrectangle 670,710 752,750 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 688,735 'Copy'",

        # Hook 3 Card (Direct Contrarian)
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,780 772,905 16,16",
        "-stroke", "none",
        "-fill", "#a855f7", "-draw", "roundrectangle 48,798 190,826 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "11",
        "-draw", "text 60,817 'HOOK #3 (CONTRARIAN ANGLE)'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 48,854 '\"Stop buying expensive cameras — this $0 prompt out-performed my $3K lens.\"'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 48,885 'High conversion CTA included'",
        "-fill", "#10b981", "-draw", "roundrectangle 670,850 752,890 8,8",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 688,875 'Copy'",

        # Hashtags & SEO Package Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,920 772,1060 16,16",
        "-stroke", "none",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 48,952 '#️⃣ Platform-Optimized Hashtags & SEO Tags'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 48,990 '#contentcreator #viraltips #videoediting #creatorgrowth2026'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 48,1020 '#tiktokgrowth #youtubeshorts #aitools #videohacks'",

        # Multi-Action Footer in App
        "-fill", "#6366f1", "-draw", "roundrectangle 28,1080 390,1130 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 75,1112 '🎙️ Record Voiceover'",
        "-fill", "#10b981", "-draw", "roundrectangle 410,1080 772,1130 12,12",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 480,1112 '📅 Schedule in Calendar'",

        # Bottom Automotive HVAC System Bar (Vertical Center Console AAOS)
        "-fill", "#050912", "-draw", "rectangle 0,1190 800,1280",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 48,1242 '[Heat] 70°F'",
        "-fill", "#64748b", "-font", "DejaVu-Sans", "-pointsize", "15",
        "-draw", "text 180,1242 'Seat: [3]'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 320,1242 'AUTO  |  FAN [|||| ]  |  A/C'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 680,1242 '72°F'",
    ]
    run_magick(args, "screenshot_5_portrait_800x1280.png")

def generate_portrait_6():
    # 800 x 1280 - Vertical AAOS Center Console: AI Video Script & Teleprompter
    args = [
        "-size", "800x1280", "xc:#070b14",
        # Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 800,56",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 28,36 '10:48 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 120,36 'PRND [P] PARKED'",
        "-fill", "#10b981", "-draw", "text 270,36 'EV 84% • STUDIO MODE'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 600,36 '5G LTE  |  Teleprompter'",

        # App Header
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 28,105 'AI Video Script Breakdown'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 28,132 'Scene-by-scene timing, voiceover cues, and camera actions for Shorts & Reels'",

        # Scene 1 Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,155 772,340 16,16",
        "-stroke", "none",
        "-fill", "#ec4899", "-draw", "roundrectangle 48,175 180,205 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,195 'SCENE 1 (0-3s)'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 200,195 'CAMERA: Fast handheld snap zoom'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 48,235 '\"Most people waste 40 hours a week on manual tasks.\"'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 48,270 'On-Screen Text: STOP WASTING 40 HOURS A WEEK'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 48,305 'Audio Cue: Bass drop sound effect on word \"WASTE\"'",

        # Scene 2 Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,355 772,550 16,16",
        "-stroke", "none",
        "-fill", "#38bdf8", "-draw", "roundrectangle 48,375 180,405 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,395 'SCENE 2 (3-9s)'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 200,395 'CAMERA: Screen recording demonstrating workflow'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 48,435 '\"Watch this: I paste one URL, and the AI builds my full campaign.\"'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 48,470 'On-Screen Text: 1 CLICK = FULL CAMPAIGN IN 30 SECONDS'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 48,510 'Audio Cue: Upbeat lo-fi background groove (30% volume)'",

        # Scene 3 Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,565 772,750 16,16",
        "-stroke", "none",
        "-fill", "#10b981", "-draw", "roundrectangle 48,585 190,615 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,605 'SCENE 3 (9-15s)'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 210,605 'CAMERA: Direct to lens speaking pose'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 48,645 '\"Click the link in my bio to get instant access for free!\"'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 48,680 'On-Screen Text: LINK IN BIO • LIMITED FREE TIERS'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 48,715 'Audio Cue: Bell chime notification sound'",

        # Big Teleprompter Full Display Card
        "-fill", "#111827", "-stroke", "#ef4444", "-strokewidth", "2",
        "-draw", "roundrectangle 28,775 772,1060 16,16",
        "-stroke", "none",
        "-fill", "#ef4444", "-draw", "roundrectangle 48,795 240,825 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,815 'LARGE TELEPROMPTER'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 48,870 '\"MOST PEOPLE WASTE 40 HOURS\"'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 48,910 '\"A WEEK ON MANUAL TASKS...\"'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "18",
        "-draw", "text 48,960 '\"Watch this: I paste one URL and the AI\"'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "18",
        "-draw", "text 48,995 '\"builds my full campaign instantly!\"'",

        "-fill", "#ef4444", "-draw", "roundrectangle 48,1015 390,1050 8,8",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 80,1038 '[REC] START IN-CAR RECORDING'",
        "-fill", "#1e293b", "-draw", "roundrectangle 410,1015 752,1050 8,8",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 455,1038 'Auto-Scroll: 1.2x Speed'",

        # Export & TTS Control Buttons
        "-fill", "#6366f1", "-draw", "roundrectangle 28,1080 390,1130 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 75,1112 '🔊 Play Vehicle TTS Voice'",
        "-fill", "#10b981", "-draw", "roundrectangle 410,1080 772,1130 12,12",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 470,1112 '💾 Export Script to Phone'",

        # Bottom HVAC
        "-fill", "#050912", "-draw", "rectangle 0,1190 800,1280",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 48,1242 '[Heat] 70°F'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 320,1242 'AUTO  |  FAN [|||| ]  |  A/C'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 680,1242 '72°F'",
    ]
    run_magick(args, "screenshot_6_portrait_800x1280.png")

def generate_portrait_7():
    # 800 x 1280 - Vertical AAOS Center Console: Google Workspace & Cloud Asset Hub
    args = [
        "-size", "800x1280", "xc:#070b14",
        # Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 800,56",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 28,36 '10:52 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 120,36 'PRND [D]'",
        "-fill", "#10b981", "-draw", "text 220,36 'EV 83% • 308 mi'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 580,36 '5G LTE  |  Google Cloud Sync'",

        # App Header
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 28,105 'Google Workspace & Cloud Asset Studio'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 28,132 'Direct Google Tasks sync, Google Drive picker, and client contact routing'",

        # Google Tasks Integration Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,155 772,420 16,16",
        "-stroke", "none",
        "-fill", "#38bdf8", "-draw", "roundrectangle 48,175 190,205 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,195 'GOOGLE TASKS'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 205,195 'Campaign Milestones & Sync'",

        # Task 1
        "-fill", "#1e293b", "-draw", "roundrectangle 48,220 752,275 10,10",
        "-fill", "#10b981", "-draw", "roundrectangle 64,236 84,256 4,4",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 98,252 'Review TikTok Ad Script for Client Alpha (Due: Today)'",
        "-fill", "#38bdf8", "-draw", "roundrectangle 630,230 736,265 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 642,252 'Generate AI'",

        # Task 2
        "-fill", "#1e293b", "-draw", "roundrectangle 48,285 752,340 10,10",
        "-fill", "#10b981", "-draw", "roundrectangle 64,301 84,321 4,4",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 98,317 'Upload Midjourney Thumbnail to YouTube Studio'",
        "-fill", "#38bdf8", "-draw", "roundrectangle 630,295 736,330 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 642,317 'Generate AI'",

        # Task 3
        "-fill", "#1e293b", "-draw", "roundrectangle 48,350 752,405 10,10",
        "-fill", "#475569", "-draw", "roundrectangle 64,366 84,386 4,4",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 98,382 'Send weekly analytics report to Brand Sponsor'",
        "-fill", "#38bdf8", "-draw", "roundrectangle 630,360 736,395 6,6",
        "-fill", "#082f49", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 642,382 'Send Draft'",

        # Google Drive & Asset Picker Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,440 772,720 16,16",
        "-stroke", "none",
        "-fill", "#f59e0b", "-draw", "roundrectangle 48,460 210,490 6,6",
        "-fill", "#451a03", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,480 'GOOGLE DRIVE'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 225,480 'Brand Guidelines & Video Assets'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,510 752,565 10,10",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,542 '[PDF] Client_Brand_Voice_Guidelines_2026.pdf'",
        "-fill", "#10b981", "-draw", "roundrectangle 620,520 736,555 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 635,542 'Use Context'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,575 752,630 10,10",
        "-fill", "#ec4899", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,607 '[MP4] Car_Interior_Hero_B-Roll_4K.mp4'",
        "-fill", "#10b981", "-draw", "roundrectangle 620,585 736,620 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 635,607 'Attach Reel'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,640 752,695 10,10",
        "-fill", "#a855f7", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,672 '[PNG] Automotive_Thumbnail_HighRes.png'",
        "-fill", "#10b981", "-draw", "roundrectangle 620,650 736,685 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 635,672 'Attach Post'",

        # Google Contacts Team & Collaborators Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,740 772,1020 16,16",
        "-stroke", "none",
        "-fill", "#10b981", "-draw", "roundrectangle 48,760 210,790 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,780 'GOOGLE CONTACTS'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 225,780 'Client Agencies & Sponsors'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,810 752,865 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,842 'Elena Vance — Agency Creative Director'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 430,842 'elena@creativemedia.io'",
        "-fill", "#6366f1", "-draw", "roundrectangle 640,820 736,855 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 655,842 'Share Script'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,875 752,930 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,907 'Marcus Brody — Brand Partnerships Lead'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 430,907 'marcus@branddealz.com'",
        "-fill", "#6366f1", "-draw", "roundrectangle 640,885 736,920 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 655,907 'Share Script'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,940 752,995 10,10",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,972 'Sarah Jenkins — Video Production Specialist'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 430,972 'sarah@motionstudio.tv'",
        "-fill", "#6366f1", "-draw", "roundrectangle 640,950 736,985 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 655,972 'Share Script'",

        # Quick Action Footer
        "-fill", "#10b981", "-draw", "roundrectangle 28,1050 772,1110 12,12",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 240,1085 '⚡ Sync All Workspace Assets & Refresh'",

        # Bottom HVAC
        "-fill", "#050912", "-draw", "rectangle 0,1190 800,1280",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 48,1242 '[Heat] 70°F'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 320,1242 'AUTO  |  FAN [|||| ]  |  A/C'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 680,1242 '72°F'",
    ]
    run_magick(args, "screenshot_7_portrait_800x1280.png")

def generate_portrait_8():
    # 800 x 1280 - Vertical AAOS Center Console: Google AdMob Suite & Affiliate Engine
    args = [
        "-size", "800x1280", "xc:#070b14",
        # Status Bar
        "-fill", "#0f172a", "-draw", "rectangle 0,0 800,56",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 28,36 '10:58 AM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 120,36 'PRND [P] SUPERCHARGING'",
        "-fill", "#10b981", "-draw", "text 320,36 'EV 86% • +120 kW'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 600,36 '5G LTE  |  AdMob Live'",

        # App Header
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "24",
        "-draw", "text 28,105 'Google AdMob Monetization Suite'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 28,132 'In-vehicle ad revenue, watch-to-earn credits, and affiliate copybuilder'",

        # AdMob Network Live Status Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,155 772,390 16,16",
        "-stroke", "none",
        "-fill", "#10b981", "-draw", "roundrectangle 48,175 220,205 6,6",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,195 'ADMOB CONNECTED'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 235,195 'Live Revenue Performance'",

        # 4 Metric Boxes
        "-fill", "#1e293b", "-draw", "roundrectangle 48,225 210,305 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 62,250 'Est. Revenue'",
        "-fill", "#10b981", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 62,288 '$1,428.50'",

        "-fill", "#1e293b", "-draw", "roundrectangle 226,225 388,305 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 240,250 'eCPM'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 240,288 '$14.80'",

        "-fill", "#1e293b", "-draw", "roundrectangle 404,225 566,305 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 418,250 'Impressions'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 418,288 '96,420'",

        "-fill", "#1e293b", "-draw", "roundrectangle 582,225 752,305 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 596,250 'Click Rate'",
        "-fill", "#ec4899", "-font", "DejaVu-Sans-Bold", "-pointsize", "22",
        "-draw", "text 596,288 '3.24%'",

        # Rewarded Ad Card (Earn while charging)
        "-fill", "#10b981", "-draw", "roundrectangle 48,325 752,375 10,10",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 140,358 '🎬 Watch Rewarded Ad: Earn +5 Free AI Credits'",

        # AdMob Native Ad Advanced Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,410 772,670 16,16",
        "-stroke", "none",
        "-fill", "#6366f1", "-draw", "roundrectangle 48,430 250,460 6,6",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,450 'NATIVE ADVANCED AD'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 270,450 'Custom In-Feed Sponsored Placement'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,475 752,650 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 68,510 'Automotive Wireless Charger & Mount 2026'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans", "-pointsize", "14",
        "-draw", "text 68,542 'Fast 15W Qi-certified magnetic car charger designed for modern EV cockpits.'",
        "-fill", "#f59e0b", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 68,574 'Rating: ★★★★★ (4.9 / 5.0)  •  Price: $39.99'",
        "-fill", "#6366f1", "-draw", "roundrectangle 68,595 240,635 8,8",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 95,620 'Order on Amazon'",
        "-fill", "#334155", "-draw", "roundrectangle 255,595 440,635 8,8",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 280,620 'AdMob Unit Active'",

        # Amazon Affiliate Assistant Card
        "-fill", "#111827", "-stroke", "#374151", "-strokewidth", "1",
        "-draw", "roundrectangle 28,690 772,1060 16,16",
        "-stroke", "none",
        "-fill", "#f59e0b", "-draw", "roundrectangle 48,710 240,740 6,6",
        "-fill", "#451a03", "-font", "DejaVu-Sans-Bold", "-pointsize", "12",
        "-draw", "text 60,730 'AMAZON AFFILIATE SUITE'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 260,730 'Instant Multi-Platform Sales Kit'",

        "-fill", "#1e293b", "-draw", "roundrectangle 48,755 752,805 10,10",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 68,788 'URL: amazon.com/dp/B09XYZ (Electric Vehicle Tire Inflator)'",

        "-fill", "#10b981", "-draw", "roundrectangle 48,820 380,865 10,10",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "14",
        "-draw", "text 75,848 '⚡ Generate Sales Copy Package'",

        "-fill", "#0f172a", "-draw", "roundrectangle 48,880 752,1040 10,10",
        "-fill", "#ec4899", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 68,910 'TIKTOK 15s HOOK:'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 68,935 '\"Never get stranded with low tire pressure again — watch this!\"'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "13",
        "-draw", "text 68,970 'YOUTUBE DESCRIPTION & SEO:'",
        "-fill", "#ffffff", "-font", "DejaVu-Sans", "-pointsize", "13",
        "-draw", "text 68,995 'Get today special discount: amzn.to/3xyz #CommissionsEarned'",
        "-fill", "#94a3b8", "-font", "DejaVu-Sans", "-pointsize", "12",
        "-draw", "text 68,1025 'SEO: emergency car gear, best tire inflators, ev roadtrip accessories'",

        # Multi-Action Footer
        "-fill", "#6366f1", "-draw", "roundrectangle 28,1080 390,1130 12,12",
        "-fill", "#ffffff", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 65,1112 '⚙️ AdMob Unit Config'",
        "-fill", "#10b981", "-draw", "roundrectangle 410,1080 772,1130 12,12",
        "-fill", "#042f2e", "-font", "DejaVu-Sans-Bold", "-pointsize", "15",
        "-draw", "text 480,1112 '📋 Copy Sales Bundle'",

        # Bottom HVAC
        "-fill", "#050912", "-draw", "rectangle 0,1190 800,1280",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 48,1242 '[Heat] 70°F'",
        "-fill", "#38bdf8", "-font", "DejaVu-Sans-Bold", "-pointsize", "16",
        "-draw", "text 320,1242 'AUTO  |  FAN [|||| ]  |  A/C'",
        "-fill", "#cbd5e1", "-font", "DejaVu-Sans-Bold", "-pointsize", "18",
        "-draw", "text 680,1242 '72°F'",
    ]
    run_magick(args, "screenshot_8_portrait_800x1280.png")

if __name__ == "__main__":
    print("Generating 8 Android Automotive OS Screenshots...")
    generate_landscape_1()
    generate_landscape_2()
    generate_landscape_3()
    generate_landscape_4()
    generate_portrait_5()
    generate_portrait_6()
    generate_portrait_7()
    generate_portrait_8()
    print("All 8 screenshots successfully created!")
