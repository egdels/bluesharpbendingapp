# Answer to the Question: Wie viele Trainingsdaten werden benötigt?

## Minimum Required Training Data

For the Harmonica Chord Detection Model, the amount of training data required depends on the desired model quality and robustness:

1. **Basic Model**:
   - At least 50-100 examples for each note type
   - This provides sufficient data for basic recognition capabilities
   - Suitable for prototype development or limited use cases

2. **Robust Model**:
   - 500+ examples for each note type
   - Provides significantly better accuracy and generalization
   - Recommended for production-ready applications

## Breakdown by Categories

The training data should be distributed across different categories:

1. **By Harmonica Key**:
   - Training data should cover all common harmonica keys (C, A, D, G, F, Bb)
   - Each key should have a proportional representation in the dataset

2. **By Note Type**:
   - Single notes (blow and draw for each channel)
   - Chords (standard chord combinations)
   - Bent notes (various bending depths)
   - Overblow and overdraw techniques (if applicable)

3. **By Dynamic Variations**:
   - Different volumes (soft, medium, loud)
   - Different playing styles and articulations

## Practical Considerations

When collecting training data, consider the following:

1. **Quality over Quantity**:
   - High-quality recordings in a quiet environment are more valuable than a larger number of poor-quality recordings
   - Each recording should clearly capture the intended note or chord

2. **Balanced Dataset**:
   - Ensure balanced representation across all categories
   - Avoid overrepresentation of certain notes or techniques

3. **Data Augmentation**:
   - The data preparation pipeline includes augmentation techniques that can effectively multiply your dataset
   - This means that 100 high-quality recordings can be augmented to create a much larger effective training set

## Conclusion

For a functional Harmonica Chord Detection Model:
- **Minimum viable dataset**: ~600-1,200 recordings (50-100 examples × 12 notes)
- **Recommended robust dataset**: ~6,000+ recordings (500+ examples × 12 notes)

The exact number may vary based on the specific requirements of your application, the diversity of playing techniques you want to recognize, and the desired accuracy level of the model.