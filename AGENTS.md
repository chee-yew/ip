  # Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Somewhat of a beginner
* IDE and level of expertise: Used intelliJ and VSCode prior to this

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## UI testing workflow

After any code change that affects the program's console behavior (a command, prompt, message, or output format), before treating the change as done:

1. Update `test/ui-test-plan.md` if the change adds, removes, or alters any user-visible command or output. Keep every case's aim, input, and expected output current.
2. Invoke the `test-ui` skill (`.codex/skills/test-ui`) to run the console UI test plan against the updated program.
3. If a test case fails, stop immediately and report that case's actual and expected output. Fix the code or the plan, then rerun the full test plan from the beginning.

Skip this workflow for changes with no observable effect on console input/output, such as internal refactors, build/config edits, or non-UI unit tests.
