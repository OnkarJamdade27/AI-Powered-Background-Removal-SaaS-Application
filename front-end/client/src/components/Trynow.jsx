const Trynow = () => {
    return (
        <div className="flex flex-col items-center justify-center bg-white px-4">

            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-7 text-center">
                Remove Image Background.
            </h2>
            <p className="text-gray-500 mb-8 text-center">
                Get a transparent background for any image.
            </p>
            <div className="bg-white rounded-2xl shadow-lg p-10 flex flex-col items-center space-y-4">
                <input type="file" id="upload2" hidden accept="image/*" />
                <label htmlFor="upload2"
                    className="bg-indigo-600 hover:bg-blue-700 text-white font-semibold flex items-center gap-2 px-6 py-2.5 rounded-full text-lg cursor-pointer transition-colors">
                    Upload Image
                </label>
                <p className="text-gray-500 text-sm">
                    or drop a file, paste image or <a href="#" className="text-indigo-600 hover:underline">URL</a>
                </p>
            </div>
        </div>
    )
}

export default Trynow;