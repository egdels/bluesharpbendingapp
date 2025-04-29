# BlueSharp Bending App Testing Documentation

This directory contains documentation for manual testing of the BlueSharp Bending App across various Android devices and screen configurations.

## Contents

1. [Test Concept](test_concept.md) - Comprehensive test plan outlining which test cases should be tested on which devices with which screen resolutions
2. [Test Results Template](test_results_template.md) - Template for documenting test results
3. [Test Results Example](test_results_example.md) - Example of a completed test results document

## How to Use These Documents

### For Test Planners

The [Test Concept](test_concept.md) document provides a comprehensive framework for planning manual tests across different Android devices and screen configurations. It includes:

- Detailed test cases organized by app feature
- Device categories and screen resolutions to test
- Prioritized test matrix mapping test cases to device configurations
- Specific test procedures for each feature
- Recommended test environment

Use this document to plan testing efforts, allocate resources, and ensure comprehensive coverage across different device types.

### For Testers

1. Review the [Test Concept](test_concept.md) document to understand the testing scope and requirements.

2. For each device you are testing on:
   - Create a copy of the [Test Results Template](test_results_template.md)
   - Name it according to the following convention: `test_results_[device_name]_[date].md` (e.g., `test_results_galaxy_s21_2023-11-15.md`)
   - Fill in all sections of the template as you perform the tests
   - Take screenshots of any issues or interesting observations
   - Document all issues found with clear reproduction steps

3. Refer to the [Test Results Example](test_results_example.md) for guidance on how to complete the template.

## Testing Guidelines

1. **Be thorough**: Test all specified cases in both portrait and landscape orientations where applicable.

2. **Document everything**: Even minor issues should be documented. Include detailed notes and observations.

3. **Take screenshots**: Visual documentation is crucial, especially for UI issues. Take screenshots of both working and non-working states.

4. **Be specific**: When documenting issues, provide exact steps to reproduce, expected results, and actual results.

5. **Test on real devices**: While emulators can be used, testing on physical devices is preferred whenever possible.

6. **Test edge cases**: Try unusual interactions and edge cases, not just the happy path.

7. **Verify fixes**: When issues are fixed, verify the fix on the same device configuration where the issue was found.

## Screenshot Organization

Store screenshots in a dedicated directory structure:

```
screenshots/
├── [device_name]/
│   ├── harp_view/
│   ├── training_view/
│   ├── settings_view/
│   └── issues/
```

Name screenshots descriptively, including the test case and orientation:
- `harp_view_note_enlargement_portrait.png`
- `issue1_offcenter_note_landscape.png`

## Submitting Test Results

After completing testing on a device:

1. Ensure your test results document is complete and follows the template format
2. Organize your screenshots according to the recommended structure
3. Submit your test results and screenshots according to your team's process

## Questions and Support

If you have questions about the testing process or documentation, please contact the test coordinator.