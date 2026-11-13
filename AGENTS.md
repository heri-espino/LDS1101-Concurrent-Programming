# AI agent guide — LDS1101 Concurrent Programming

## Repository-first workflow
Read the assignment PDF, lecture notes, README, .ai_handoff, prior code and reports, and CI before editing. Keep existing activity directory names. Preserve prior deliverables, branches, tests, numerical results and generated reports. Never merge without authorization.

## Current structure (activities 11 onward)
The newer flat layout supersedes the historical nested example in .ai_handoff: keep the original assignment PDF, normal Java files and *_ultracommented.java files, a .tex report and compiled .pdf directly in each activity folder; include build/, figures/, readme.md, and sequential experiment001/ where needed. The shared institutional LaTeX style and original UDLAP logo remain in latex_compartido/.

## Quality
Write simple readable Java without unnecessary abstractions. In ultra-commented variants, explain imports and nontrivial statements in lowercase Spanish without accents or trailing periods. Compile both variants. Verify protocols with real independent Java client and server processes, not mock output. Experiments must state purpose, method, reproduction, findings, and next steps.

Reports must include every assignment section, actual test results, diagrams, and explicit answers. Keep authentic screenshots separate from console-log text; never invent screenshots. Compile with latexmk, inspect errors/overfull warnings, visually inspect all pages, and commit source, results, PDF and build artifacts (but never secrets or .class files). Maintain README, AGENTS.md and .ai_handoff. Use non-force ref updates and respect concurrent agents.
