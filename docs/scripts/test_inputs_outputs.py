#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import tensorflow as tf
import sys

def inspect_model(saved_model_path, input_signature_name="serving_default"):
    """
    Inspect the saved model to view input and output tensors dynamically.

    Args:
        saved_model_path (str): Path to the saved model directory.
        input_signature_name (str): Name of the input signature, default is 'serving_default'.
    """
    # Modell laden
    try:
        model = tf.saved_model.load(saved_model_path)
        signatures = model.signatures[input_signature_name]

        # Informationen zu Eingabe- und Ausgabeknoten abrufen
        print("Graph Details:")
        print("Inputs:")
        for input_tensor in signatures.inputs:
            print(f"Name: {input_tensor.name}, Shape: {input_tensor.shape}, DType: {input_tensor.dtype}")

        print("Outputs:")
        for output_tensor in signatures.outputs:
            print(f"Name: {output_tensor.name}, Shape: {output_tensor.shape}, DType: {output_tensor.dtype}")
    except KeyError:
        print(f"Error: Signature '{input_signature_name}' not found in the model.")
        print(f"Available signatures: {list(model.signatures.keys())}")


if __name__ == "__main__":
    # Modellpfad und Signaturname aus den Argumenten entnehmen
    if len(sys.argv) < 2:
        print("Usage: python inspect_model.py <saved_model_path> [<signature_name>]")
        sys.exit(1)

    saved_model_path = sys.argv[1]
    signature_name = sys.argv[2] if len(sys.argv) > 2 else "serving_default"

    inspect_model(saved_model_path, signature_name)