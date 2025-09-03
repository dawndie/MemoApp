#!/bin/bash

# Gemini CLI Context Summarization Hook for Claude Code
# This hook automatically summarizes large context sections using Gemini CLI

# Configuration (can be overridden by environment variables)
GEMINI_MODEL="${GEMINI_MODEL:-gemini-1.5-flash}"
MAX_CONTEXT_TOKENS="${MAX_CONTEXT_TOKENS:-8000}"
SUMMARY_TARGET_WORDS="${SUMMARY_TARGET_WORDS:-500}"

# Function to estimate token count (rough heuristic: 1 token ≈ 4 characters)
estimate_tokens() {
    local text="$1"
    local char_count=$(echo -n "$text" | wc -c)
    echo $((char_count / 4))
}

# Function to count words
count_words() {
    local text="$1"
    echo "$text" | wc -w
}

# Main hook logic
main() {
    # Read input from stdin
    local input=$(cat)
    
    # Estimate token count
    local estimated_tokens=$(estimate_tokens "$input")
    
    # Check if summarization is needed
    if [ "$estimated_tokens" -le "$MAX_CONTEXT_TOKENS" ]; then
        # Below threshold, pass through unchanged
        echo "$input"
        echo "Hook: Context size ($estimated_tokens tokens) below threshold, no summarization needed" >&2
        return 0
    fi
    
    # Log summarization attempt
    echo "Hook: Large context detected ($estimated_tokens tokens), summarizing with Gemini..." >&2
    
    # Prepare summarization prompt
    local prompt="Please provide a concise technical summary of the following content. Focus on:
- Key functions, classes, and their purposes
- Important data structures and algorithms
- Critical dependencies and imports
- Main architectural patterns
- Essential configuration or setup details

Target length: approximately $SUMMARY_TARGET_WORDS words. Maintain technical accuracy while being concise.

Content to summarize:
$input"
    
    # Call Gemini CLI for summarization
    local summary
    if ! summary=$(echo "$input" | gemini -p "Please provide a concise technical summary of the following content. Focus on: key functions, classes, and their purposes; important data structures and algorithms; critical dependencies and imports; main architectural patterns; essential configuration or setup details. Target length: approximately $SUMMARY_TARGET_WORDS words. Maintain technical accuracy while being concise." 2>/dev/null); then
        echo "Hook: Gemini CLI failed, falling back to original content" >&2
        echo "$input"
        return 0
    fi
    
    # Calculate token savings
    local summary_tokens=$(estimate_tokens "$summary")
    local savings=$((estimated_tokens - summary_tokens))
    local savings_percent=$((savings * 100 / estimated_tokens))
    
    # Output the summary
    echo "$summary"
    
    # Log results to stderr
    echo "Hook: Summarization complete" >&2
    echo "  - Original: $estimated_tokens tokens" >&2
    echo "  - Summary: $summary_tokens tokens" >&2
    echo "  - Saved: $savings tokens ($savings_percent%)" >&2
}

# Execute main function
main