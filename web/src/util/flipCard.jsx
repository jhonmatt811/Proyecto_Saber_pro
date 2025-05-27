import { useState } from "react";

const FlipCard = ({ frontContent, backContent }) => {
  const [flipped, setFlipped] = useState(false);

  return (
    <div
      onClick={() => setFlipped((f) => !f)}
      // perspectiva 3D
      style={{ perspective: 1000 }}
      className="inline-block cursor-pointer"
    >
      <div
        // transición + preserve-3d + toggle de rotación Y
        className={`
          relative w-80 h-48
          transition-transform duration-500
          transform
          [transform-style:preserve-3d]
          ${flipped ? "[transform:rotateY(180deg)]" : ""}
        `}
      >
        {/* Cara frontal */}
        <div
          className={`
            absolute inset-0
            rounded-lg overflow-hidden
            bg-white
            shadow-lg
            p-4
            flex items-center justify-center
            [backface-visibility:hidden]
          `}
        >
          {frontContent}
        </div>

        {/* Cara trasera */}
        <div
          className={`
            absolute inset-0
            rounded-lg overflow-hidden
            bg-primary-400 text-white
            shadow-lg
            p-4
            flex items-center justify-center
            [transform:rotateY(180deg)]
            [backface-visibility:hidden]
          `}
        >
          {backContent}
        </div>
      </div>
    </div>
  );
};

export default FlipCard;
