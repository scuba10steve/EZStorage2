# AI Agent Guidelines

This document outlines the guidelines for AI agents interacting with this repository. Adhering to these principles ensures consistent, safe, and efficient collaboration.

## Purpose

The primary purpose of an AI agent in this repository is to assist human developers by automating tasks, providing insights, and maintaining code quality, while strictly following established project conventions and best practices.

## Core Mandates

1.  **Adherence to Conventions:** Always prioritize and rigorously adhere to existing project conventions (code style, naming, architecture, documentation) when reading or modifying code. Analyze surrounding code, tests, and configuration files (e.g., `package.json`, `Cargo.toml`, `requirements.txt`, `build.gradle`) to understand established patterns.
2.  **Library/Framework Verification:** Never assume a library or framework is available or appropriate. Verify its established usage within the project by checking imports, configuration files, or observing neighboring files before employing it.
3.  **Style & Structure Mimicry:** Mimic the style (formatting, naming), structure, framework choices, typing, and architectural patterns of existing code in the project.
4.  **Idiomatic Changes:** Understand the local context (imports, functions/classes) to ensure changes integrate naturally and idiomatically.
5.  **Comments:** Add code comments sparingly. Focus on *why* something is done, especially for complex logic, rather than *what* is done. Do not edit comments separate from the code being changed.
6.  **Proactiveness:** Fulfill user requests thoroughly, including adding tests for new features or bug fixes. Consider all created files (especially tests) as permanent artifacts unless instructed otherwise.
7.  **Confirm Ambiguity/Expansion:** Do not take significant actions beyond the clear scope of the request without confirming with the user.
8.  **Security:** Prioritize security best practices. Never introduce code that exposes sensitive information.
9.  **No Unrequested Commits:** **NEVER** stage or commit changes unless explicitly instructed by the user.
10.  **No Attribution on Commits:** **NEVER** add a Co-Authored section to a commit.


## Tool Usage

*   **File System Operations:** Use `read_file`, `write_file`, and `replace` tools for file manipulation. Always read a file before modifying it to ensure accuracy.
*   **Code Search:** Utilize `search_file_content` for efficient codebase searching.
*   **Shell Commands:** Use `run_shell_command` for executing shell commands. Always explain the purpose and potential impact of commands that modify the file system or system state before execution. Prefer non-interactive commands.
*   **Efficient Output:** When using `run_shell_command`, prefer flags that reduce output verbosity. Redirect large outputs to temporary files if necessary.

## Issue Creation

*   **Always use issue templates.** This repository has issue templates in `.github/ISSUE_TEMPLATE/`. When creating issues, use `gh issue create --template bug_report.yml` for bugs or `gh issue create --template feature_request.yml` for feature requests. Never create issues with plain `--body` that bypasses the template structure.
*   **Bug reports** must include: mod version, Minecraft version, description, expected behavior, and steps to reproduce.
*   **Feature requests** must include: description and motivation. Alternatives considered is optional but encouraged.
*   **Project board:** All new issues must be added to the "Kanban" board in the "Steve's Simple Storage" GitHub project so they are tracked and visible on the board.

## Version & Release Management

*   **Version Bumps:** Never manually edit `mod_version` in `gradle.properties`. Use the `bump-version` GitHub Actions workflow (`gh workflow run bump-version.yml --ref main -f part=<patch|minor|major>`).
*   **Releases:** Trigger the `release` GitHub Actions workflow (`gh workflow run release.yml --ref main -f release_type=<beta|release>`). This builds, creates a GitHub Release, and publishes to Modrinth and CurseForge.
*   **Workflow Order:** Always run `bump-version` first and wait for it to complete before triggering `release`, so the release picks up the correct version.

## Workflow Example (Software Engineering Tasks)

1.  **Understand:** Analyze the request and context using search and file reading tools.
2.  **Plan:** Formulate a coherent plan. Break down complex tasks. Share the plan if beneficial for user understanding. Include testing in the plan.
3.  **Implement:** Execute the plan using available tools, adhering to conventions.
4.  **Verify (Tests):** Run relevant tests.
5.  **Verify (Standards):** Run build, linting, and type-checking commands.
6.  **Finalize:** Await further instructions.
