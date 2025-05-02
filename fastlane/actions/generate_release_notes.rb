require 'fastlane/action'

module Fastlane
  module Actions
    class GenerateReleaseNotesAction < Action
      def self.run(params)
        # Get the last tag
        last_tag = Actions.sh("git describe --tags --abbrev=0 2>/dev/null || echo 'none'", log: false).strip
        
        UI.message("Last tag: #{last_tag}")
        
        # Get the commits since the last tag
        if last_tag == 'none'
          UI.message("No previous tags found, using all commits")
          commits = Actions.sh("git log --pretty=format:'%s' --no-merges", log: false).strip.split("\n")
        else
          UI.message("Getting commits since #{last_tag}")
          commits = Actions.sh("git log #{last_tag}..HEAD --pretty=format:'%s' --no-merges", log: false).strip.split("\n")
        end
        
        # Skip empty commit lists
        if commits.empty? || commits.first.empty?
          UI.message("No commits found")
          return "No changes since last release."
        end
        
        # Categorize commits
        features = []
        fixes = []
        others = []
        
        commits.each do |commit|
          if commit.downcase.start_with?('feat', 'feature', 'add')
            features << "- #{commit}"
          elsif commit.downcase.start_with?('fix', 'bug', 'issue')
            fixes << "- #{commit}"
          else
            others << "- #{commit}"
          end
        end
        
        # Generate release notes
        notes = []
        
        notes << "## What's New" unless features.empty?
        notes.concat(features) unless features.empty?
        
        notes << "\n## Fixes" unless fixes.empty?
        notes.concat(fixes) unless fixes.empty?
        
        notes << "\n## Other Changes" unless others.empty?
        notes.concat(others) unless others.empty?
        
        release_notes = notes.join("\n")
        
        # Save to file if path provided
        if params[:output_path]
          File.write(params[:output_path], release_notes)
          UI.success("Release notes saved to #{params[:output_path]}")
        end
        
        return release_notes
      end

      def self.description
        "Generates release notes based on Git commits since the last tag"
      end

      def self.authors
        ["Your Name"]
      end

      def self.available_options
        [
          FastlaneCore::ConfigItem.new(key: :output_path,
                                       env_name: "RELEASE_NOTES_OUTPUT_PATH",
                                       description: "Path to save the release notes",
                                       optional: true,
                                       type: String)
        ]
      end

      def self.is_supported?(platform)
        true
      end
    end
  end
end