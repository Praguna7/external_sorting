def validate_sorted_file(file_path):
    """
    Validate whether a file containing numbers is sorted in ascending order.
    :param file_path: Path to the file to validate.
    :return: True if the file is sorted, False otherwise.
    """
    try:
        with open(file_path, 'r') as file:
            previous_number = None
            
            for line in file:
                try:
                    current_number = int(line.strip())
                except ValueError:
                    print(f"Invalid line encountered (not an integer): {line}")
                    return False

                if previous_number is not None and current_number < previous_number:
                    print(f"File is not sorted. {current_number} comes after {previous_number}.")
                    return False
                
                previous_number = current_number

        print("File is sorted.")
        return True
    
    except FileNotFoundError:
        print(f"File not found: {file_path}")
        return False
    except Exception as e:
        print(f"An error occurred: {str(e)}")
        return False


for i in range(3):
    file_path = "sorted_files/quicksorted_output_"+str(i+1)+".txt"  # Replace with the path to your sorted file
    print("Validating "+ file_path)
    validate_sorted_file(file_path)

    file_path = "sorted_files/mergesorted_output_"+str(i+1)+".txt"  # Replace with the path to your sorted file
    print("Validating "+ file_path)
    validate_sorted_file(file_path)
