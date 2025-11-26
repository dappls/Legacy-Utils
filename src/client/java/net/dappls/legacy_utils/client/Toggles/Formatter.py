# coords_to_java_matrix.py

def parse_coords(nums):
    if len(nums) % 3 != 0:
        print("⚠️ Warning: Number of values is not a multiple of 3.")
    groups = [nums[i:i+3] for i in range(0, len(nums), 3)]
    java_matrix = "{" + ", ".join(
        "{" + ", ".join(str(n) for n in triple) + "}" for triple in groups
    ) + "}"
    return java_matrix

if __name__ == "__main__":
    print("Paste coordinates, separated by commas (x,y,z,...). Type 'done' when finished:")
    all_nums = []

    while True:
        line = input("> ").strip()
        if line.lower() == "done":
            break
        # Remove spaces, split by commas
        parts = line.replace(" ", "").split(",")
        for p in parts:
            try:
                all_nums.append(int(p))
            except ValueError:
                print(f"⚠️ Skipping invalid number: '{p}'")

    if all_nums:
        result = parse_coords(all_nums)
        print("\n✅ Java-formatted matrix:")
        print(result)
    else:
        print("No valid coordinates were entered.")