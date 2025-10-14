#!/bin/bash

# 📊 JaCoCo Code Coverage Commands for NovaBook
# Author: Luis Alfredo - Clan Cienaga
# Usage: bash jacoco-commands.sh [command]

echo "🎯 JaCoCo Coverage Commands for NovaBook"
echo "========================================="
echo ""

case "$1" in
    "test")
        echo "🔍 Running tests with coverage report..."
        mvn clean test
        echo ""
        echo "✅ Report generated at: target/site/jacoco/index.html"
        ;;
        
    "report")
        echo "📊 Generating coverage report only..."
        mvn jacoco:report
        echo ""
        echo "✅ Report updated at: target/site/jacoco/index.html"
        ;;
        
    "verify")
        echo "✅ Running tests with coverage verification..."
        mvn clean verify
        ;;
        
    "view")
        echo "🌐 Opening coverage report in browser..."
        if command -v xdg-open > /dev/null; then
            xdg-open target/site/jacoco/index.html
        elif command -v open > /dev/null; then
            open target/site/jacoco/index.html
        else
            echo "Manual: file://$(pwd)/target/site/jacoco/index.html"
        fi
        ;;
        
    "summary")
        echo "📈 Coverage Summary (from CSV):"
        echo "==============================="
        if [ -f "target/site/jacoco/jacoco.csv" ]; then
            echo ""
            echo "🏆 TOP COVERED CLASSES:"
            tail -n +2 target/site/jacoco/jacoco.csv | \
            awk -F',' 'BEGIN{OFS=","} {
                if($5 + $6 > 0) {
                    coverage = $6 / ($5 + $6) * 100;
                    printf "%-30s %3.0f%% (%d/%d lines)\n", $3, coverage, $6, $5+$6
                }
            }' | sort -k2 -nr | head -10
            echo ""
            echo "📊 TOTAL PROJECT STATS:"
            tail -n +2 target/site/jacoco/jacoco.csv | \
            awk -F',' '{
                total_missed += $5; total_covered += $6;
                branch_missed += $7; branch_covered += $8;
            } END {
                line_coverage = total_covered / (total_missed + total_covered) * 100;
                branch_coverage = branch_covered / (branch_missed + branch_covered) * 100;
                printf "Lines:    %3.1f%% (%d/%d)\n", line_coverage, total_covered, total_missed + total_covered;
                printf "Branches: %3.1f%% (%d/%d)\n", branch_coverage, branch_covered, branch_missed + branch_covered;
            }'
        else
            echo "❌ No coverage data found. Run 'bash jacoco-commands.sh test' first."
        fi
        ;;
        
    "clean")
        echo "🧹 Cleaning coverage data..."
        rm -rf target/site/jacoco/
        rm -f target/jacoco.exec
        echo "✅ Coverage data cleaned."
        ;;
        
    "help" | "")
        echo "Available commands:"
        echo ""
        echo "  test     - Run tests and generate coverage report"
        echo "  report   - Generate coverage report from existing data"  
        echo "  verify   - Run tests with coverage thresholds check"
        echo "  view     - Open HTML report in browser"
        echo "  summary  - Show coverage summary in terminal"
        echo "  clean    - Remove all coverage data"
        echo "  help     - Show this help message"
        echo ""
        echo "Examples:"
        echo "  bash jacoco-commands.sh test"
        echo "  bash jacoco-commands.sh summary" 
        echo "  bash jacoco-commands.sh view"
        echo ""
        echo "📁 Reports Location: target/site/jacoco/"
        echo "🌐 HTML Report:      target/site/jacoco/index.html"
        echo "📊 CSV Data:         target/site/jacoco/jacoco.csv"
        ;;
        
    *)
        echo "❌ Unknown command: $1"
        echo "Run 'bash jacoco-commands.sh help' for usage information."
        exit 1
        ;;
esac