require 'fastlane/action'

module Fastlane
  module Actions
    class IncrementRootVersionAction < Action
      def self.run(params)
        gradle_file_path = params[:gradle_file_path]
        version_type = params[:version_type]

        UI.user_error!("Gradle file path not specified") unless gradle_file_path
        UI.user_error!("Version type not specified") unless version_type
        UI.user_error!("Version type must be one of: major, minor, patch") unless ["major", "minor", "patch"].include?(version_type)

        # Read the current version from the build.gradle file
        UI.message("Reading current version from #{gradle_file_path}")
        file_content = File.read(gradle_file_path)
        
        # Extract the current version
        version_match = file_content.match(/version\s*=\s*['"](.*)['"]/)
        UI.user_error!("Could not find version in #{gradle_file_path}") unless version_match
        
        current_version = version_match[1]
        UI.message("Current version: #{current_version}")
        
        # Split the version into major, minor, patch
        version_parts = current_version.split('.')
        UI.user_error!("Version format should be major.minor.patch") unless version_parts.length == 3
        
        major = version_parts[0].to_i
        minor = version_parts[1].to_i
        patch = version_parts[2].to_i
        
        # Increment the appropriate part
        case version_type
        when "major"
          major += 1
          minor = 0
          patch = 0
        when "minor"
          minor += 1
          patch = 0
        when "patch"
          patch += 1
        end
        
        # Create the new version string
        new_version = "#{major}.#{minor}.#{patch}"
        UI.message("New version: #{new_version}")
        
        # Replace the version in the file
        new_content = file_content.gsub(/version\s*=\s*['"](.*)['"]/, "version = '#{new_version}'")
        File.write(gradle_file_path, new_content)
        
        UI.success("Successfully incremented #{version_type} version to #{new_version}")
        return new_version
      end

      def self.description
        "Increments the version in the root build.gradle file"
      end

      def self.authors
        ["Your Name"]
      end

      def self.available_options
        [
          FastlaneCore::ConfigItem.new(key: :gradle_file_path,
                                       env_name: "GRADLE_FILE_PATH",
                                       description: "Path to the root build.gradle file",
                                       optional: false,
                                       type: String),
          FastlaneCore::ConfigItem.new(key: :version_type,
                                       env_name: "VERSION_TYPE",
                                       description: "The type of version increment (major, minor, patch)",
                                       optional: false,
                                       verify_block: proc do |value|
                                         UI.user_error!("Version type must be one of: major, minor, patch") unless ["major", "minor", "patch"].include?(value)
                                       end,
                                       type: String)
        ]
      end

      def self.is_supported?(platform)
        true
      end
    end
  end
end